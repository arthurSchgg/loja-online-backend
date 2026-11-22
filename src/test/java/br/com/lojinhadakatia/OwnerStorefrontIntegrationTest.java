package br.com.lojinhadakatia;

import br.com.lojinhadakatia.media.PhotoCodec;
import br.com.lojinhadakatia.media.MediaService;
import br.com.lojinhadakatia.whatsapp.service.MessageTemplateService;
import br.com.lojinhadakatia.shared.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** All media and commercial fixture data are synthetic and rolled back. */
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test") @Transactional
class OwnerStorefrontIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate jdbc;
    @Autowired PhotoCodec codec;
    @Autowired MessageTemplateService templates;
    @BeforeEach void init(){
        jdbc.update("DELETE FROM featured_products");
        jdbc.update("DELETE FROM storefront_settings");
        jdbc.update("INSERT INTO storefront_settings(id,featured_customized) VALUES(1,FALSE)");
        jdbc.update("DELETE FROM media_capacity");
        jdbc.update("INSERT INTO media_capacity(id) VALUES(1)");
        jdbc.update("DELETE FROM whatsapp_message_templates");
    }
    private MockHttpServletRequestBuilder admin(MockHttpServletRequestBuilder request){
        return request.with(user("tester").roles("ADMIN")).with(csrf());
    }
    private byte[] photo(Color color,boolean alpha,int width,int height) throws Exception {
        var image=new BufferedImage(width,height,alpha?BufferedImage.TYPE_INT_ARGB:BufferedImage.TYPE_INT_RGB);
        var g=image.createGraphics();g.setColor(color);g.fillRect(0,0,width,height);g.dispose();
        var bytes=new ByteArrayOutputStream();ImageIO.write(image,alpha?"png":"jpg",bytes);image.flush();return bytes.toByteArray();
    }
    private MockMultipartFile file(byte[] data){return new MockMultipartFile("file","fixture.jpg","image/jpeg",data);}
    private String upload(byte[] bytes) throws Exception{
        return json.readTree(mvc.perform(admin(multipart("/api/v1/admin/media").file(file(bytes))))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.used").value(false))
            .andReturn().getResponse().getContentAsString()).get("url").asText();
    }
    private long create(String path,String body) throws Exception {
        return json.readTree(mvc.perform(admin(post(path)).contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).get("id").asLong();
    }
    private ResultActions save(String path,String body) throws Exception{
        return mvc.perform(admin(put(path)).contentType(MediaType.APPLICATION_JSON).content(body));
    }
    @Test void optimizedPhotosArePrivateUntilReferencedAndSafelyReusable() throws Exception{
        byte[] data=photo(Color.PINK,false,1900,1100);
        mvc.perform(multipart("/api/v1/admin/media").file(file(data)).with(csrf())).andExpect(status().isUnauthorized());
        mvc.perform(multipart("/api/v1/admin/media").file(file(data)).with(user("tester").roles("ADMIN"))).andExpect(status().isForbidden());
        var path=upload(data);
        assertEquals(path,upload(data));
        String adminPath=path.replace("/api/v1/media/","/api/v1/admin/media/");
        mvc.perform(get(path)).andExpect(status().isNotFound());
        mvc.perform(get(adminPath)).andExpect(status().isUnauthorized());
        mvc.perform(admin(get(adminPath))).andExpect(status().isOk())
            .andExpect(content().contentType("image/jpeg")).andExpect(header().string("Cache-Control","no-store"));
        mvc.perform(admin(get("/api/v1/admin/media"))).andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].width").value(1600)).andExpect(jsonPath("$.content[0].used").value(false));
        long brand=create("/api/v1/admin/brands","{\"name\":\"Marca fotos teste\"}");
        long category=create("/api/v1/admin/categories","{\"name\":\"Categoria fotos teste\"}");
        String request="""
          {"name":"Perfume foto teste","brandId":%d,"categoryIds":[%d],"price":59.90,
           "published":true,"available":true,"images":[{"url":"%s","position":0,"altText":"Frasco"}]}
          """.formatted(brand,category,path);
        long product=create("/api/v1/admin/products",request);
        var response=mvc.perform(get(path)).andExpect(status().isOk()).andExpect(content().contentType("image/jpeg"))
            .andExpect(header().exists("ETag")).andReturn().getResponse();
        assertTrue(response.getContentAsByteArray().length<=PhotoCodec.MAX_OUTPUT);
        mvc.perform(get(path).header("If-None-Match",response.getHeader("ETag"))).andExpect(status().isNotModified());
        mvc.perform(admin(get("/api/v1/admin/products").param("q","foto teste").param("brandId",String.valueOf(brand))
            .param("categoryId",String.valueOf(category)).param("published","true"))).andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(product));
        mvc.perform(admin(delete(adminPath))).andExpect(status().isConflict());
        save("/api/v1/admin/products/"+product,request.replace("\"published\":true","\"published\":false"))
            .andExpect(status().isOk());
        mvc.perform(get(path)).andExpect(status().isNotFound());
        mvc.perform(admin(delete(adminPath))).andExpect(status().isConflict());
        save("/api/v1/admin/products/"+product,request.replace("\"published\":true","\"published\":false")
            .replace("\"images\":[{\"url\":\""+path+"\",\"position\":0,\"altText\":\"Frasco\"}]","\"images\":[]"))
            .andExpect(status().isOk());
        mvc.perform(admin(delete(adminPath))).andExpect(status().isNoContent());
        mvc.perform(admin(get(adminPath))).andExpect(status().isNotFound());
        mvc.perform(admin(multipart("/api/v1/admin/media").file(file("not a photograph".getBytes())))).andExpect(status().isConflict());
    }
    @Test void offeringCoverAndContentCampaignReferencesWork() throws Exception{
        String first=upload(photo(Color.ORANGE,false,320,200));
        String second=upload(photo(new Color(60,90,100,128),true,100,100));
        var path="/api/v1/admin/offerings";
        String body="""
          {"title":"Vela teste galeria","type":"CANDLE","price":40,"published":true,"available":true,
          "images":[{"url":"%s","position":1},{"url":"%s","position":0}]}
          """.formatted(first,second);
        long id=create(path,body);
        mvc.perform(get("/api/v1/offerings/"+id)).andExpect(status().isOk())
            .andExpect(jsonPath("$.imageUrl").value(second)).andExpect(jsonPath("$.images[1].url").value(first));
        mvc.perform(get(first)).andExpect(status().isOk());mvc.perform(get(second)).andExpect(content().contentType("image/png"));
        save(path+"/"+id,"""
          {"title":"Vela teste galeria","type":"CANDLE","price":40,"published":true,"available":true,"imageUrl":"%s"}
          """.formatted(second)).andExpect(status().isOk()).andExpect(jsonPath("$.images.length()").value(2));
        save(path+"/"+id,body.replace("\"published\":true","\"published\":false")).andExpect(status().isOk());
        mvc.perform(get(first)).andExpect(status().isNotFound());
        create("/api/v1/admin/content-sections","""
            {"slug":"owner-foto-teste","title":"História teste","published":true,"imageUrl":"%s"}
            """.formatted(first));
        mvc.perform(get(first)).andExpect(status().isOk());
        create("/api/v1/admin/campaigns","""
            {"title":"Banner teste","published":true,"position":0,"imageUrl":"%s","linkUrl":"/catalogo.html"}
            """.formatted(second));
        mvc.perform(get(second)).andExpect(status().isOk());
        mvc.perform(admin(delete(second.replace("/media/","/admin/media/")))).andExpect(status().isConflict());
    }
    @Test void ownerControlsFeaturedOrderAndTemplates() throws Exception{
        long brand=create("/api/v1/admin/brands","{\"name\":\"Marca seleção teste\"}");
        String item="""
            {"name":"%s","price":39,"brandId":%d,"categoryIds":[],"published":true,"available":true,
             "images":[{"url":"https://example.invalid/foto.jpg","position":0}]}
            """;
        long a=create("/api/v1/admin/products",item.formatted("A teste",brand));
        long b=create("/api/v1/admin/products",item.formatted("B teste",brand));
        mvc.perform(get("/api/v1/storefront/featured")).andExpect(status().isOk()).andExpect(jsonPath("$.customized").value(false));
        mvc.perform(get("/api/v1/admin/storefront/featured")).andExpect(status().isUnauthorized());
        String selected="{\"customized\":true,\"productIds\":["+a+","+b+"]}";
        save("/api/v1/admin/storefront/featured",selected).andExpect(status().isOk());
        mvc.perform(get("/api/v1/storefront/featured")).andExpect(jsonPath("$.products[0].id").value(a))
            .andExpect(jsonPath("$.products[1].id").value(b));
        save("/api/v1/admin/storefront/featured","{\"customized\":true,\"productIds\":["+a+","+a+"]}").andExpect(status().isConflict());
        save("/api/v1/admin/products/"+a,item.formatted("A teste",brand).replace("\"published\":true","\"published\":false"))
            .andExpect(status().isOk());
        mvc.perform(get("/api/v1/storefront/featured")).andExpect(jsonPath("$.products.length()").value(1)).andExpect(jsonPath("$.products[0].id").value(b));
        mvc.perform(admin(get("/api/v1/admin/whatsapp-templates"))).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(4));
        save("/api/v1/admin/whatsapp-templates/PRODUCT","{\"template\":\"Oi, quero {{nome}} por {{preco}}!\"}").andExpect(status().isOk());
        assertEquals("Oi, quero perfume por R$ 39,00!",templates.render(MessageTemplateService.Kind.PRODUCT,"perfume","R$ 39,00",""));
        save("/api/v1/admin/whatsapp-templates/PRODUCT","{\"template\":\"Oi {{nome}}\"}").andExpect(status().isConflict());
        save("/api/v1/admin/whatsapp-templates/CANDLE","{\"template\":\"Oi {{nome}} {{preco}} {{personalizacao}}\"}").andExpect(status().isOk());
        save("/api/v1/admin/whatsapp-templates/CANDLE","{\"template\":\"Oi {{nome}} {{preco}} {{desconhecida}}\"}").andExpect(status().isConflict());
        assertEquals("{{preco}} R$ 5,00",MessageTemplateService.expand("{{nome}} {{preco}}","{{preco}}","R$ 5,00",""));
    }
    @Test void photoCodecBoundsAndNormalizesRaster() throws Exception {
        assertThrows(BusinessException.class,()->codec.sanitize(new byte[0]));
        assertThrows(BusinessException.class,()->codec.sanitize("not photo".getBytes()));
        var p=codec.sanitize(photo(new Color(50,100,90,64),true,32,24));
        assertEquals("image/png",p.contentType());assertEquals(32,p.width());assertEquals(24,p.height());
        assertTrue(p.bytes().length>0);assertTrue(p.bytes().length<=PhotoCodec.MAX_OUTPUT);
    }
}
