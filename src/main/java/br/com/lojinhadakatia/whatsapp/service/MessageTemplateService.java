package br.com.lojinhadakatia.whatsapp.service;

import br.com.lojinhadakatia.shared.exception.BusinessException;
import java.util.*;
import java.util.regex.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Text-only owner templates. Variables are expanded once with confirmed server-side values. */
@Service @Transactional(readOnly=true)
public class MessageTemplateService {
    public enum Kind {PRODUCT,CANDLE,WORKSHOP,RENTAL}
    public record Template(Kind kind,String template,String defaultTemplate,List<String> requiredVariables){}
    private static final Pattern TOKEN=Pattern.compile("\\{\\{([a-z]+)\\}\\}");
    private final JdbcTemplate jdbc;
    public MessageTemplateService(JdbcTemplate jdbc){this.jdbc=jdbc;}
    public static String defaultTemplate(Kind kind){
        return switch(kind){
            case PRODUCT -> "Olá! Gostaria de saber mais sobre o produto: {{nome}}. Preço atual: {{preco}}.";
            case CANDLE -> "Olá! Gostaria de saber mais sobre: {{nome}}. {{preco}}.{{personalizacao}}";
            default -> "Olá! Gostaria de saber mais sobre: {{nome}}. {{preco}}.";
        };
    }
    public static List<String> required(Kind kind){
        return kind==Kind.CANDLE?List.of("nome","preco","personalizacao"):List.of("nome","preco");
    }
    public static void validate(Kind kind,String text){
        if(text==null||text.isBlank()||text.length()>1200||
           text.codePoints().anyMatch(c->Character.isISOControl(c)&&c!='\n'&&c!='\r'&&c!='\t'))
            throw new BusinessException("Escreva uma mensagem de até 1200 caracteres.");
        var matcher=TOKEN.matcher(text);
        Set<String> found=new HashSet<>();
        while(matcher.find()){
            if(!required(kind).contains(matcher.group(1)))throw new BusinessException("Variável de mensagem não reconhecida.");
            found.add(matcher.group(1));
        }
        String remaining=TOKEN.matcher(text).replaceAll("");
        if(remaining.indexOf('{')>=0||remaining.indexOf('}')>=0||!found.containsAll(required(kind)))
            throw new BusinessException("Inclua todas as variáveis obrigatórias sem modificar seus nomes.");
    }
    private String stored(Kind kind){
        return jdbc.query("SELECT template FROM whatsapp_message_templates WHERE kind=?",(rs,n)->rs.getString(1),kind.name())
            .stream().findFirst().orElse(defaultTemplate(kind));
    }
    public Template get(Kind kind){return new Template(kind,stored(kind),defaultTemplate(kind),required(kind));}
    public List<Template> list(){return Arrays.stream(Kind.values()).map(this::get).toList();}
    @Transactional public Template save(Kind kind,String text){
        validate(kind,text);
        // Update first for both PostgreSQL and the test database.
        if(jdbc.update("UPDATE whatsapp_message_templates SET template=? WHERE kind=?",text,kind.name())==0)
            jdbc.update("INSERT INTO whatsapp_message_templates(kind,template) VALUES(?,?)",kind.name(),text);
        return get(kind);
    }
    public static String expand(String template,String name,String price,String personalization){
        Map<String,String> values=Map.of("nome",name,"preco",price,"personalizacao",personalization);
        var matcher=TOKEN.matcher(template);
        return matcher.replaceAll(match->Matcher.quoteReplacement(values.get(match.group(1))));
    }
    public String render(Kind kind,String name,String price,String personalization){
        return expand(stored(kind),name,price,personalization);
    }
}
