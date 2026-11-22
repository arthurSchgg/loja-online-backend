package br.com.lojinhadakatia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
@Transactional
class CatalogCommercialIntegrityIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    private JsonNode create(String path,String body) throws Exception {
        return mapper.readTree(mvc.perform(post(path).with(user("tester").roles("ADMIN")).with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
    }
    private long brand() throws Exception {
        return create("/api/v1/admin/brands","{\"name\":\"Marca "+UUID.randomUUID()+"\"}").get("id").asLong();
    }
    private long category() throws Exception {
        return create("/api/v1/admin/categories","{\"name\":\"Categoria "+UUID.randomUUID()+"\"}").get("id").asLong();
    }
    private String product(String name,long brand,String categoryIds,String priceFields,boolean published) {
        return """
            {"name":"%s","brandId":%d,"categoryIds":%s,%s,
             "available":true,"published":%s,
             "images":[{"url":"https://example.invalid/product.webp","position":0}]}
            """.formatted(name,brand,categoryIds,priceFields,published);
    }
    private long createProduct(String name,long brand,String categories,String priceFields,boolean published) throws Exception {
        return create("/api/v1/admin/products",product(name,brand,categories,priceFields,published)).get("id").asLong();
    }

    @Test void preservesBundledCatalogPhotosOnAdministrativeEdits() throws Exception {
        long brand=brand();
        String image="assets/images/catalogo/pdf-2026/produto-teste.webp";
        String request=product("Foto do catálogo",brand,"[]","\"price\":25",true)
            .replace("https://example.invalid/product.webp",image);
        long id=create("/api/v1/admin/products",request).get("id").asLong();
        mvc.perform(put("/api/v1/admin/products/"+id).with(user("tester").roles("ADMIN")).with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content(request))
            .andExpect(status().isOk()).andExpect(jsonPath("$.images[0].url").value(image));
    }

    @Test void pendingPriceIsExplicitAndCannotBecomeAFreeConsultation() throws Exception {
        long brand=brand();
        var pending=create("/api/v1/admin/products",product("Sem preço",brand,"[]",
            "\"price\":null,\"priceStatus\":\"ON_REQUEST\"",true));
        long id=pending.get("id").asLong();
        create("/api/v1/admin/products/"+id+"/stock/movements",
            "{\"type\":\"ENTRY\",\"quantity\":2,\"reason\":\"Inventário fictício\"}");
        mvc.perform(get("/api/v1/admin/products/"+id).with(user("tester").roles("ADMIN")))
            .andExpect(jsonPath("$.manualAvailable").value(true))
            .andExpect(jsonPath("$.stockQuantity").value(2))
            .andExpect(jsonPath("$.available").value(false));
        mvc.perform(get("/api/v1/products/"+id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.priceStatus").value("ON_REQUEST"))
            .andExpect(jsonPath("$.price").value(nullValue()))
            .andExpect(jsonPath("$.effectivePrice").value(nullValue()))
            .andExpect(jsonPath("$.stockQuantity").value(0))
            .andExpect(jsonPath("$.available").value(false))
            .andExpect(jsonPath("$.manualAvailable").doesNotExist());
        mvc.perform(get("/api/v1/products/"+id+"/whatsapp")).andExpect(status().isConflict());
        mvc.perform(post("/api/v1/whatsapp/confirm").contentType(MediaType.APPLICATION_JSON)
            .content("{\"type\":\"PRODUCT\",\"id\":"+id+",\"expectedPrice\":0}"))
            .andExpect(status().isConflict());
        mvc.perform(post("/api/v1/admin/products").with(user("tester").roles("ADMIN")).with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content(product("Zero",brand,"[]","\"price\":0",true)))
            .andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/admin/products").with(user("tester").roles("ADMIN")).with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content(product("Sem decisão",brand,"[]","\"price\":null",true)))
            .andExpect(status().isBadRequest());
        mvc.perform(put("/api/v1/admin/products/"+id).with(user("tester").roles("ADMIN")).with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content(product("Preço confirmado",brand,"[]","\"price\":35.50",true)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.manualAvailable").value(true))
            .andExpect(jsonPath("$.stockQuantity").value(2));
        mvc.perform(get("/api/v1/products/"+id))
            .andExpect(jsonPath("$.available").value(true))
            .andExpect(jsonPath("$.stockQuantity").value(2))
            .andExpect(jsonPath("$.priceStatus").value("CONFIRMED"));
    }

    @Test void unknownPricesSortLastAndNeverMatchPriceCeiling() throws Exception {
        long brand=brand();
        long known=createProduct("Preço cadastrado",brand,"[]","\"price\":40",true);
        long pending=createProduct("Valor pendente",brand,"[]","\"price\":null,\"priceStatus\":\"ON_REQUEST\"",true);
        for(String sort:new String[]{"price-asc","price-desc"}){
            mvc.perform(get("/api/v1/products").param("brandId",String.valueOf(brand)).param("sort",sort))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(known))
                .andExpect(jsonPath("$.content[1].id").value(pending));
        }
        mvc.perform(get("/api/v1/products").param("brandId",String.valueOf(brand)).param("maxPrice","100"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.content[0].id").value(known));
    }

    @Test void productPriceEditsCannotInvalidateAnExistingPromotion() throws Exception {
        long brand=brand();
        long id=createProduct("Promovido",brand,"[]","\"price\":100",true);
        create("/api/v1/admin/products/"+id+"/promotions",
            "{\"promotionalPrice\":60,\"startsAt\":\"2020-01-01T00:00:00Z\",\"endsAt\":\"2099-01-01T00:00:00Z\"}");
        for(String next:new String[]{"\"price\":40","\"price\":null,\"priceStatus\":\"ON_REQUEST\""}){
            mvc.perform(put("/api/v1/admin/products/"+id).with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(product("Promovido",brand,"[]",next,true)))
                .andExpect(status().isConflict());
        }
        mvc.perform(get("/api/v1/products/"+id)).andExpect(jsonPath("$.effectivePrice").value(60));
    }

    @Test void relatedProductsSearchTheEntirePublishedCatalog() throws Exception {
        long originalBrand=brand(),otherBrand=brand(),shared=category();
        long original=createProduct("Original",originalBrand,"["+shared+"]","\"price\":50",true);
        long matchingBrand=createProduct("Mesma marca",originalBrand,"[]","\"price\":50",true);
        long matchingCategory=createProduct("Mesma categoria",otherBrand,"["+shared+"]","\"price\":50",true);
        createProduct("Rascunho",originalBrand,"["+shared+"]","\"price\":50",false);
        for(int i=0;i<13;i++)createProduct("Não relacionado "+i,otherBrand,"[]","\"price\":50",true);
        mvc.perform(get("/api/v1/products/"+original+"/related"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(matchingBrand))
            .andExpect(jsonPath("$[1].id").value(matchingCategory));
    }
}
