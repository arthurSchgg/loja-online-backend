package br.com.lojinhadakatia.catalog.service;

import br.com.lojinhadakatia.catalog.dto.*;
import br.com.lojinhadakatia.catalog.entity.*;
import br.com.lojinhadakatia.catalog.repository.*;
import br.com.lojinhadakatia.shared.exception.*;
import jakarta.persistence.criteria.Join;
import java.net.URI;
import java.math.BigDecimal;
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
    public CatalogService(ProductRepository products,BrandRepository brands,CategoryRepository categories){
        this.products=products;this.brands=brands;this.categories=categories;
    }
    public List<LookupDto> brands(){return brands.findByActiveTrueOrderByNameAsc().stream().map(b->new LookupDto(b.getId(),b.getName())).toList();}
    public List<LookupDto> categories(){return categories.findByActiveTrueOrderByNameAsc().stream().map(c->new LookupDto(c.getId(),c.getName())).toList();}
    public Page<ProductResponse> search(String query,Long brandId,Long categoryId,Boolean available,Pageable pageable,boolean admin){
        Specification<Product> filter=(root,cq,cb)->{
            var predicates=new ArrayList<jakarta.persistence.criteria.Predicate>();
            if(!admin)predicates.add(cb.isTrue(root.get("published")));
            if(query!=null&&!query.isBlank())predicates.add(cb.like(cb.lower(root.get("name")),"%"+query.trim().toLowerCase(Locale.ROOT).replace("%","\\%").replace("_","\\_")+"%",'\\'));
            if(brandId!=null)predicates.add(cb.equal(root.get("brand").get("id"),brandId));
            if(categoryId!=null){Join<Product,Category> cat=root.join("categories");predicates.add(cb.equal(cat.get("id"),categoryId));cq.distinct(true);}
            if(available!=null)predicates.add(cb.equal(root.get("available"),available));
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
        return new ProductResponse(p.getId(),p.getName(),p.getDescription(),p.getPrice(),p.isAvailable(),p.isPublished(),
            new LookupDto(p.getBrand().getId(),p.getBrand().getName()),
            p.getCategories().stream().map(c->new LookupDto(c.getId(),c.getName())).sorted(Comparator.comparing(LookupDto::name)).toList(),
            p.getImages().stream().map(i->new ImageResponse(i.getImageUrl(),i.getPosition(),i.getAltText())).toList());
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
