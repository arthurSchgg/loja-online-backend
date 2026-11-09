package br.com.lojinhadakatia;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class CandleCustomizationIntegrationTest {
 @Autowired MockMvc mvc;
 @Autowired ObjectMapper mapper;

 private long candle() throws Exception {
  String result=mvc.perform(post("/api/v1/admin/offerings")
    .with(user("tester").roles("ADMIN")).with(csrf())
    .contentType(MediaType.APPLICATION_JSON)
    .content("""
      {"title":"Vela personalizavel","type":"CANDLE","price":null,"published":true}
      """))
    .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
  return mapper.readTree(result).get("id").asLong();
 }
 private void configure(long id) throws Exception {
  mvc.perform(put("/api/v1/admin/offerings/"+id+"/options")
    .with(user("tester").roles("ADMIN")).with(csrf())
    .contentType(MediaType.APPLICATION_JSON)
    .content("""
      {"options":[
       {"kind":"AROMA","value":"Baunilha","active":true},
       {"kind":"AROMA","value":"Flor","active":false},
       {"kind":"PACKAGING","value":"Papel kraft","active":true}
      ]}
      """))
    .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(3));
 }
 private void confirm(long id,String selections,int expected) throws Exception {
  mvc.perform(post("/api/v1/whatsapp/confirm")
    .contentType(MediaType.APPLICATION_JSON)
    .content("""
     {"type":"OFFERING","id":%d,"expectedPrice":null,"selections":%s}
     """.formatted(id,selections)))
    .andExpect(status().is(expected));
 }
 @Test void publishedOptionsRequireValidSelectedAromaAndPackaging() throws Exception {
  mvc.perform(put("/api/v1/admin/store")
    .with(user("tester").roles("ADMIN")).with(csrf())
    .contentType(MediaType.APPLICATION_JSON)
    .content("{\"displayName\":\"Loja de teste\",\"whatsapp\":\"5547999999999\"}"))
    .andExpect(status().isOk());
  long id=candle();
  configure(id);
  mvc.perform(get("/api/v1/offerings/"+id+"/options"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.length()").value(2));
  mvc.perform(get("/api/v1/admin/offerings/"+id+"/options")
    .with(user("tester").roles("ADMIN")))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.length()").value(3));
  confirm(id,"{}",409);
  confirm(id,"{\"AROMA\":\"Flor\",\"PACKAGING\":\"Papel kraft\"}",409);
  confirm(id,"{\"AROMA\":\"Baunilha\",\"PACKAGING\":\"Papel kraft\",\"COLOR\":\"Rosa\"}",409);
  String ok=mvc.perform(post("/api/v1/whatsapp/confirm")
    .contentType(MediaType.APPLICATION_JSON)
    .content("""
     {"type":"OFFERING","id":%d,"expectedPrice":null,
      "selections":{"AROMA":"Baunilha","PACKAGING":"Papel kraft"}}
     """.formatted(id)))
    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
  var message=mapper.readTree(ok).get("message").asText();
  org.junit.jupiter.api.Assertions.assertTrue(message.contains("Aroma: Baunilha"));
  org.junit.jupiter.api.Assertions.assertTrue(message.contains("Embalagem: Papel kraft"));
  org.junit.jupiter.api.Assertions.assertTrue(message.contains("Valor sob consulta"));
 }
 @Test void rejectsAnonymousOptionEditsAndNonCandleConfiguration() throws Exception {
  long id=candle();
  mvc.perform(put("/api/v1/admin/offerings/"+id+"/options")
    .with(csrf()).contentType(MediaType.APPLICATION_JSON)
    .content("{\"options\":[]}"))
   .andExpect(status().isUnauthorized());
  configure(id);
  mvc.perform(put("/api/v1/admin/offerings/"+id+"/options")
    .with(user("tester").roles("ADMIN")).with(csrf())
    .contentType(MediaType.APPLICATION_JSON)
    .content("""
      {"options":[{"kind":"AROMA","value":"Baunilha","active":true},
                  {"kind":"AROMA","value":"Baunilha","active":true}]}
      """))
    .andExpect(status().isConflict());
 }
}
