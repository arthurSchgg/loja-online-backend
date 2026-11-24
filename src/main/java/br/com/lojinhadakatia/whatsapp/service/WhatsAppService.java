package br.com.lojinhadakatia.whatsapp.service;

import br.com.lojinhadakatia.catalog.service.CatalogService;
import br.com.lojinhadakatia.catalog.dto.ProductResponse;
import br.com.lojinhadakatia.catalog.dto.ProductPriceStatus;
import br.com.lojinhadakatia.offering.dto.OfferingResponse;
import br.com.lojinhadakatia.offering.service.OfferingService;
import br.com.lojinhadakatia.store.repository.StoreInformationRepository;
import br.com.lojinhadakatia.whatsapp.dto.WhatsAppLinkResponse;
import br.com.lojinhadakatia.whatsapp.dto.WhatsAppConfirmationRequest;
import java.math.BigDecimal;
import br.com.lojinhadakatia.shared.exception.BusinessException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Constrói links de atendimento sem registrar pedidos ou dados pessoais.
 * Telefone publicado vem exclusivamente das configurações da loja.
 */
@Service
@Transactional(readOnly = true)
public class WhatsAppService {
    private final StoreInformationRepository storeRepository;
    private final CatalogService catalogService;
    private final OfferingService offeringService;
    private final MessageTemplateService templates;

    public WhatsAppService(StoreInformationRepository storeRepository,
                           CatalogService catalogService,
                           OfferingService offeringService,MessageTemplateService templates) {
        this.storeRepository = storeRepository;
        this.catalogService = catalogService;
        this.offeringService = offeringService;
        this.templates = templates;
    }

    /** Uma consulta só é permitida para produtos atualmente publicados. */
    public WhatsAppLinkResponse forProduct(Long productId) {
        var product = catalogService.published(productId);
        requireConfirmedProduct(product);
        return productLink(product);
    }

    /** Uma consulta só é permitida para serviços atualmente publicados. */
    public WhatsAppLinkResponse forOffering(Long offeringId) {
        var offering = offeringService.publicOne(offeringId);
        if (!offering.available()) throw new BusinessException("Serviço indisponível para consulta.");
        return offeringLink(offering);
    }

    /** Revalida dados atuais para o usuário confirmar antes de abrir o WhatsApp. */
    public WhatsAppLinkResponse confirm(WhatsAppConfirmationRequest request) {
        switch (request.type()) {
            case PRODUCT -> {
                if (request.customizations()!=null && !request.customizations().isEmpty())
                    throw new BusinessException("Produtos não aceitam opções de velas.");
                var product = catalogService.published(request.id());
                requireConfirmedProduct(product);
                if (request.expectedPrice() == null ||
                    product.effectivePrice().compareTo(request.expectedPrice()) != 0)
                    throw new BusinessException("O preço do produto mudou; atualize a confirmação.");
                // Use the checked snapshot rather than re-fetching between validation and response.
                return productLink(product);
            }
            case OFFERING -> {
                var offering = offeringService.publicOne(request.id());
                if (!offering.available()) throw new BusinessException("Serviço indisponível para consulta.");
                BigDecimal actual = offering.price();
                BigDecimal expected = request.expectedPrice();
                if ((actual == null) != (expected == null) ||
                    (actual != null && actual.compareTo(expected) != 0))
                    throw new BusinessException("O valor da oferta mudou; atualize a confirmação.");
                var selections = offeringService.validateCustomizations(offering, request.customizations());
                if (selections.isEmpty()) return offeringLink(offering);
                String formatted = actual == null ? "Valor sob consulta" :
                    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(actual);
                String options = selections.entrySet().stream()
                    .map(item -> item.getKey()+": "+item.getValue())
                    .collect(java.util.stream.Collectors.joining("; "));
                return buildLink(templates.render(MessageTemplateService.Kind.CANDLE,offering.title(),formatted,
                    " Personalização: " + options + ". Preço final a confirmar no WhatsApp."));
            }
            default -> throw new BusinessException("Tipo de consulta inválido.");
        }
    }

    private void requireConfirmedProduct(ProductResponse product) {
        if (product.priceStatus()==ProductPriceStatus.ON_REQUEST ||
            product.effectivePrice()==null || product.effectivePrice().signum()<=0)
            throw new BusinessException("O preço deste produto ainda não foi confirmado.");
        if (!product.available()) throw new BusinessException("Produto indisponível para consulta.");
    }

    private WhatsAppLinkResponse productLink(ProductResponse product) {
        return buildLink(templates.render(MessageTemplateService.Kind.PRODUCT,product.name(),
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(product.effectivePrice()),""));
    }

    private WhatsAppLinkResponse offeringLink(OfferingResponse offering) {
        String formatted = offering.price() == null ? "Valor sob consulta" :
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(offering.price());
        return buildLink(templates.render(MessageTemplateService.Kind.valueOf(offering.type().name()),
            offering.title(),formatted,""));
    }

    private WhatsAppLinkResponse buildLink(String message) {
        String phone = storeRepository.findById(1L)
            .map(info -> info.getWhatsapp())
            .filter(number -> !number.isBlank())
            .orElseThrow(() -> new BusinessException("O WhatsApp da loja ainda não foi configurado."));
        String digits = phone.replaceAll("[^0-9]", "");
        String encodedText = URLEncoder.encode(message, StandardCharsets.UTF_8)
            .replace("+", "%20");
        return new WhatsAppLinkResponse("https://wa.me/" + digits + "?text=" + encodedText, message);
    }
}
