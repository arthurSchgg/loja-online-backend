package br.com.lojinhadakatia.whatsapp.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/admin/whatsapp-templates")
public class MessageTemplateController {
    public record Request(@NotBlank @Size(max=1200) String template){}
    private final MessageTemplateService service;
    public MessageTemplateController(MessageTemplateService service){this.service=service;}
    @GetMapping public List<MessageTemplateService.Template> list(){return service.list();}
    @PutMapping("/{kind}") public MessageTemplateService.Template save(@PathVariable MessageTemplateService.Kind kind,
        @Valid @RequestBody Request request){return service.save(kind,request.template());}
}
