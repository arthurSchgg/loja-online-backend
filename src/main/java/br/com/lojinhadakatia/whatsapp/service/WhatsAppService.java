package br.com.lojinhadakatia.whatsapp.service;

import br.com.lojinhadakatia.catalog.service.CatalogService;
import br.com.lojinhadakatia.offering.service.OfferingService;
import br.com.lojinhadakatia.offering.service.CandleOptionService;
import br.com.lojinhadakatia.offering.entity.OfferingType;
import java.util.Map;
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
    private final CandleOptionService candleOptions;

    public WhatsAppService(StoreInformationRepository storeRepository,
                           CatalogService catalogService,
                           OfferingService offeringService,
                           CandleOptionService candleOptions) {
        this.storeRepository = storeRepository;
        this.catalogService = catalogService;
        this.offeringService = offeringService;
        this.candleOptions = candleOptions;
    }

    /** Uma consulta só é permitida para produtos atualmente publicados. */
    public WhatsAppLinkResponse forProduct(Long productId) {
        var product = catalogService.published(productId);
        if (!product.available()) throw new BusinessException("Produto indisponível para consulta.");
        return buildLink("Olá! Gostaria de saber mais sobre o produto: " + product.name() +
            ". Preço atual: " + NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(product.effectivePrice()) + ".");
    }

    /** Uma consulta só é permitida para serviços atualmente publicados. */
    public WhatsAppLinkResponse forOffering(Long offeringId) {
        var offering = offeringService.publicOne(offeringId);
        if (!offering.available()) throw new BusinessException("Serviço indisponível para consulta.");
        String price = offering.price() == null ? "Valor sob consulta" :
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(offering.price());
        return buildLink("Olá! Gostaria de saber mais sobre: " + offering.title() + ". " + price + ".");
    }

    /** Revalida dados atuais para o usuário confirmar antes de abrir o WhatsApp. */
    public WhatsAppLinkResponse confirm(WhatsAppConfirmationRequest request) {
        switch (request.type()) {
            case PRODUCT -> {
                var product = catalogService.published(request.id());
                if (!product.available()) throw new BusinessException("Produto indisponível para consulta.");
                if (request.expectedPrice() == null ||
                    product.effectivePrice().compareTo(request.expectedPrice()) != 0)
                    throw new BusinessException("O preço do produto mudou; atualize a confirmação.");
                return forProduct(request.id());
            }
            case OFFERING -> {
                var offering = offeringService.publicOne(request.id());
                if (!offering.available()) throw new BusinessException("Serviço indisponível para consulta.");
                BigDecimal actual = offering.price();
                BigDecimal expected = request.expectedPrice();
                if ((actual == null) != (expected == null) ||
                    (actual != null && actual.compareTo(expected) != 0))
                    throw new BusinessException("O valor da oferta mudou; atualize a confirmação.");
                String selection = "";
                if (offering.type() == OfferingType.CANDLE) {
                    selection = candleOptions.validateSelection(request.id(), request.options());
                } else if (request.options() != null && !request.options().isEmpty()) {
                    throw new BusinessException("Personalização disponível somente para velas.");
                }
                if (selection.isEmpty()) return forOffering(request.id());
                String price = actual == null ? "Valor sob consulta" :
                    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(actual);
                return buildLink("Olá! Gostaria de saber mais sobre: " + offering.title() +
                    ". " + price + ". Personalização escolhida: " + selection + ".");
            }
            default -> throw new BusinessException("Tipo de consulta inválido.");
        }
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
