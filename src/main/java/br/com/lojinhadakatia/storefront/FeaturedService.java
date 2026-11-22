package br.com.lojinhadakatia.storefront;

import br.com.lojinhadakatia.catalog.dto.ProductResponse;
import br.com.lojinhadakatia.catalog.service.CatalogService;
import br.com.lojinhadakatia.shared.exception.BusinessException;
import jakarta.validation.constraints.*;
import java.util.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional(readOnly=true)
public class FeaturedService {
    public record Selection(boolean customized,List<ProductResponse> products){}
    public record Request(boolean customized,@NotNull @Size(max=8) List<@NotNull @Positive Long> productIds){}
    private final JdbcTemplate jdbc;
    private final CatalogService catalog;
    public FeaturedService(JdbcTemplate jdbc,CatalogService catalog){this.jdbc=jdbc;this.catalog=catalog;}
    private boolean customized(){
        return jdbc.query("SELECT featured_customized FROM storefront_settings WHERE id=1",(rs,n)->rs.getBoolean(1))
            .stream().findFirst().orElse(false);
    }
    private List<Long> selected(){
        return jdbc.queryForList("SELECT product_id FROM featured_products ORDER BY position",Long.class);
    }
    private List<ProductResponse> defaults(){
        return catalog.search(null,null,null,null,PageRequest.of(0,8,Sort.by("id").descending()),false,"recent",null).getContent();
    }
    public Selection publicSelection(){
        boolean custom=customized();
        return new Selection(custom,custom?catalog.selectedProducts(selected(),false):defaults());
    }
    public Selection adminSelection(){
        return new Selection(customized(),catalog.selectedProducts(selected(),true));
    }
    @Transactional public Selection replace(Request request){
        if(jdbc.query("SELECT id FROM storefront_settings WHERE id=1 FOR UPDATE",(rs,n)->rs.getInt(1)).isEmpty())
            throw new BusinessException("Configuração de destaques indisponível.");
        var ids=request.productIds();
        if(new HashSet<>(ids).size()!=ids.size())
            throw new BusinessException("Cada produto pode aparecer apenas uma vez nos destaques.");
        // Save only existing published products; later unpublishing also hides them automatically.
        if(catalog.selectedProducts(ids,false).size()!=ids.size())
            throw new BusinessException("Selecione apenas produtos publicados.");
        jdbc.update("DELETE FROM featured_products");
        for(int i=0;i<ids.size();i++)
            jdbc.update("INSERT INTO featured_products(product_id,position) VALUES(?,?)",ids.get(i),i);
        jdbc.update("UPDATE storefront_settings SET featured_customized=? WHERE id=1",request.customized());
        return adminSelection();
    }
}
