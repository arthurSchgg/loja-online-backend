package br.com.lojinhadakatia.catalog.service;

import br.com.lojinhadakatia.catalog.dto.*;
import br.com.lojinhadakatia.catalog.entity.*;
import br.com.lojinhadakatia.catalog.repository.*;
import br.com.lojinhadakatia.shared.exception.*;
import jakarta.persistence.criteria.Join;
import java.net.URI;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Casos de uso do catálogo, com fronteira explícita entre público e admin. */
@Service
@Transactional(readOnly=true)
public class CatalogService {
    private final ProductRepository products;
    private final BrandRepository brands;
    private final CategoryRepository categories;
    private final ProductStockMovementRepository stockMovements;
    private final PromotionService promotionService;
    public CatalogService(ProductRepository products,BrandRepository brands,CategoryRepository categories,ProductStockMovementRepository stockMovements,PromotionService promotionService){
        this.products=products;this.brands=brands;this.categories=categories;this.stockMovements=stockMovements;this.promotionService=promotionService;
    }
    public List<LookupDto> brands(){return brands.findByActiveTrueOrderByNameAsc().stream().map(b->new LookupDto(b.getId(),b.getName())).toList();}
    public List<LookupDto> categories(){return categories.findByActiveTrueOrderByNameAsc().stream().map(c->new LookupDto(c.getId(),c.getName())).toList();}

    /** Includes disabled brands and categories in administrative queries. */
    public List<LookupDto> adminBrands() {
        return brands.findAll(Sort.by("name")).stream()
            .map(brand -> new LookupDto(brand.getId(), brand.getName())).toList();
    }

    public List<LookupDto> adminCategories() {
        return categories.findAll(Sort.by("name")).stream()
            .map(category -> new LookupDto(category.getId(), category.getName())).toList();
    }

    public LookupDto adminBrand(Long id) {
        Brand brand = brands.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Marca não encontrada."));
        return new LookupDto(brand.getId(), brand.getName());
    }

    public LookupDto adminCategory(Long id) {
        Category category = categories.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));
        return new LookupDto(category.getId(), category.getName());
    }

    @Transactional
    public void activateBrand(Long id) {
        Brand brand = brands.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Marca não encontrada."));
        brand.setActive(true);
    }

    @Transactional
    public void activateCategory(Long id) {
        Category category = categories.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));
        category.setActive(true);
    }
    public Page<ProductResponse> search(String query,Long brandId,Long categoryId,Boolean available,Pageable pageable,boolean admin){
        return search(query,brandId,categoryId,available,pageable,admin,"recent",null);
    }

    /** Public ordering uses a correlated active-promotion price, not just the base price.
     * Capture one instant per request so the result and pagination use a consistent
     * effective-price expression. Internal/admin callers retain their existing sort.
     */
    public Page<ProductResponse> search(String query,Long brandId,Long categoryId,Boolean available,
                                        Pageable pageable,boolean admin,String order,BigDecimal maxPrice){
        final boolean priceOrder=!admin&&("price-asc".equals(order)||"price-desc".equals(order));
        final Instant now=Instant.now();
        Specification<Product> filter=(root,cq,cb)->{
            var predicates=new ArrayList<jakarta.persistence.criteria.Predicate>();
            if(!admin)predicates.add(cb.isTrue(root.get("published")));
            if (query != null && !query.isBlank()) {
                // Escapes LIKE wildcards and searches both title and description.
                String term = query.trim().toLowerCase(Locale.ROOT)
                    .replace("\\", "\\\\")
                    .replace("%", "\\%")
                    .replace("_", "\\_");
                String pattern = "%" + term + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), pattern, '\\'),
                    cb.like(cb.lower(root.get("description")), pattern, '\\')
                ));
            }
            if(brandId!=null)predicates.add(cb.equal(root.get("brand").get("id"),brandId));
            if(categoryId!=null){
                // product_categories has PRIMARY KEY (product_id, category_id), so
                // matching one category produces at most one row per product.
                // DISTINCT would break PostgreSQL ordering by effective-price subquery.
                Join<Product,Category> cat=root.join("categories");
                predicates.add(cb.equal(cat.get("id"),categoryId));
            }
            if(available!=null){
                var inStock=cb.greaterThan(root.<Integer>get("stockQuantity"),0);
                var manuallyAvailable=cb.isTrue(root.get("available"));
                predicates.add(available?cb.and(manuallyAvailable,inStock):cb.or(cb.not(manuallyAvailable),cb.not(inStock)));
            }
            if(!admin&&(priceOrder||maxPrice!=null)){
                // At most one active promotion per product is enforced when promotions
                // are created/updated; no memory-side ordering of incomplete pages.
                var active=cq.subquery(BigDecimal.class);
                var promotion=active.from(ProductPromotion.class);
                active.select(promotion.get("promotionalPrice"));
                active.where(cb.equal(promotion.get("product"),root),
                    cb.lessThanOrEqualTo(promotion.<Instant>get("startsAt"),now),
                    cb.greaterThan(promotion.<Instant>get("endsAt"),now));
                var effective=cb.coalesce(active,root.<BigDecimal>get("price"));
                if(maxPrice!=null)predicates.add(cb.lessThanOrEqualTo(effective,maxPrice));
                // Spring Data runs a separate COUNT query; never ORDER BY in that query.
                if(priceOrder&&cq.getResultType()!=Long.class&&cq.getResultType()!=long.class){
                    cq.orderBy("price-asc".equals(order)?cb.asc(effective):cb.desc(effective),
                        cb.desc(root.get("id")));
                }
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
        return products.findAll(filter,pageable).map(this::map);
    }
    public ProductResponse published(Long id){
        Product p=products.findById(id).filter(Product::isPublished).orElseThrow(()->new ResourceNotFoundException("Produto não encontrado."));
        return map(p);
    }
    public ProductResponse adminFind(Long id){return map(findProduct(id));}
    @Transactional
    public ProductResponse save(ProductRequest request){
        Brand brand=activeBrand(request.brandId());
        Set<Category> selected=activeCategories(request.categoryIds());
        Product product=new Product(request.name().trim(),request.description(),request.price(),brand);
        product.update(request.name().trim(),request.description(),request.price(),brand,selected,request.available(),request.published());
        product.replaceImages(images(request.images()));
        return map(products.saveAndFlush(product));
    }
    @Transactional
    public ProductResponse update(Long id,ProductRequest request){
        Product product=findProduct(id);
        product.update(request.name().trim(),request.description(),request.price(),activeBrand(request.brandId()),activeCategories(request.categoryIds()),request.available(),request.published());
        product.replaceImages(images(request.images()));
        return map(products.saveAndFlush(product));
    }
    @Transactional
    public void delete(Long id){
        Product product=findProduct(id);
        if(product.isPublished())throw new BusinessException("Despublique o produto antes de excluir.");
        if(stockMovements.existsByProductId(id))throw new BusinessException("Produto com histórico de estoque não pode ser excluído; arquive-o.");
        if(promotionService.hasPromotions(id))throw new BusinessException("Produto com promoções vinculadas não pode ser excluído; arquive-o.");
        products.delete(product);
    }
    private Product findProduct(Long id){return products.findById(id).orElseThrow(()->new ResourceNotFoundException("Produto não encontrado."));}
    private Brand activeBrand(Long id){return brands.findById(id).filter(Brand::isActive).orElseThrow(()->new BusinessException("Marca inexistente ou inativa."));}
    private Set<Category> activeCategories(Set<Long> ids){
        var found=new LinkedHashSet<>(categories.findAllById(ids));
        if(found.size()!=ids.size()||found.stream().anyMatch(c->!c.isActive()))throw new BusinessException("Categoria inexistente ou inativa.");
        return found;
    }
    private List<ProductImage> images(List<ImageRequest> requests){
        if(requests.size()>12)throw new BusinessException("Limite de 12 imagens por produto.");
        return requests.stream().map(item->{
            URI uri;
            try {uri=URI.create(item.url());}catch(Exception e){throw new BusinessException("URL de imagem inválida.");}
            if(!"https".equalsIgnoreCase(uri.getScheme())||uri.getHost()==null||uri.getUserInfo()!=null)
                throw new BusinessException("A imagem precisa ser uma URL pública HTTPS.");
            return new ProductImage(item.url(),item.position(),item.altText());
        }).toList();
    }
    private ProductResponse map(Product p){
        return new ProductResponse(p.getId(),p.getName(),p.getDescription(),p.getPrice(),(p.isAvailable()&&p.getStockQuantity()>0),p.isPublished(),
            new LookupDto(p.getBrand().getId(),p.getBrand().getName()),
            p.getCategories().stream().map(c->new LookupDto(c.getId(),c.getName())).sorted(Comparator.comparing(LookupDto::name)).toList(),
            p.getImages().stream().map(i->new ImageResponse(i.getImageUrl(),i.getPosition(),i.getAltText())).toList(),
            promotionService.effectivePrice(p));
    }
    @Transactional
    public LookupDto addBrand(LookupRequest request){
        String name=request.name().trim();
        if(brands.existsByNameIgnoreCase(name))throw new BusinessException("Marca já cadastrada.");
        Brand b=brands.save(new Brand(name));return new LookupDto(b.getId(),b.getName());
    }
    @Transactional
    public LookupDto renameBrand(Long id,LookupRequest request){
        Brand b=brands.findById(id).orElseThrow(()->new ResourceNotFoundException("Marca não encontrada."));
        if(!b.getName().equalsIgnoreCase(request.name().trim())&&brands.existsByNameIgnoreCase(request.name().trim()))throw new BusinessException("Marca já cadastrada.");
        b.rename(request.name().trim());return new LookupDto(b.getId(),b.getName());
    }
    @Transactional
    public void deactivateBrand(Long id){
        Brand b=brands.findById(id).orElseThrow(()->new ResourceNotFoundException("Marca não encontrada."));b.setActive(false);
    }
    @Transactional
    public LookupDto addCategory(LookupRequest request){
        String name=request.name().trim();
        if(categories.existsByNameIgnoreCase(name))throw new BusinessException("Categoria já cadastrada.");
        Category c=categories.save(new Category(name));return new LookupDto(c.getId(),c.getName());
    }
    @Transactional
    public LookupDto renameCategory(Long id,LookupRequest request){
        Category c=categories.findById(id).orElseThrow(()->new ResourceNotFoundException("Categoria não encontrada."));
        if(!c.getName().equalsIgnoreCase(request.name().trim())&&categories.existsByNameIgnoreCase(request.name().trim()))throw new BusinessException("Categoria já cadastrada.");
        c.rename(request.name().trim());return new LookupDto(c.getId(),c.getName());
    }
    @Transactional
    public void deactivateCategory(Long id){
        Category c=categories.findById(id).orElseThrow(()->new ResourceNotFoundException("Categoria não encontrada."));c.setActive(false);
    }
}
