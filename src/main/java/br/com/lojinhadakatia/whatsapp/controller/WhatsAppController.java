package br.com.lojinhadakatia.whatsapp.controller;

import br.com.lojinhadakatia.whatsapp.dto.WhatsAppLinkResponse;
import br.com.lojinhadakatia.whatsapp.service.WhatsAppService;
import org.springframework.web.bind.annotation.*;

/**
 * Links públicos para atendimento externo contextualizado.
 * Não inicia conversas automaticamente e não guarda dados de visitantes.
 */
@RestController
@RequestMapping("/api/v1")
public class WhatsAppController {
    private final WhatsAppService service;

    public WhatsAppController(WhatsAppService service) {
        this.service = service;
    }

    @GetMapping("/products/{id}/whatsapp")
    public WhatsAppLinkResponse productLink(@PathVariable Long id) {
        return service.forProduct(id);
    }

    @GetMapping("/offerings/{id}/whatsapp")
    public WhatsAppLinkResponse offeringLink(@PathVariable Long id) {
        return service.forOffering(id);
    }
}
