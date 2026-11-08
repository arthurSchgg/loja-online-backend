package br.com.lojinhadakatia.whatsapp.controller;

import br.com.lojinhadakatia.whatsapp.dto.WhatsAppLinkResponse;
import br.com.lojinhadakatia.whatsapp.dto.WhatsAppConfirmationRequest;
import jakarta.validation.Valid;
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

    /** Apenas prepara texto/link após conferir preço vigente. Não envia mensagem. */
    @PostMapping("/whatsapp/confirm")
    public WhatsAppLinkResponse confirm(@Valid @RequestBody WhatsAppConfirmationRequest request) {
        return service.confirm(request);
    }

    @GetMapping("/offerings/{id}/whatsapp")
    public WhatsAppLinkResponse offeringLink(@PathVariable Long id) {
        return service.forOffering(id);
    }
}
