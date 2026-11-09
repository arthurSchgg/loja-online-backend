package br.com.lojinhadakatia.offering.service;

import br.com.lojinhadakatia.offering.dto.*;
import br.com.lojinhadakatia.offering.entity.*;
import br.com.lojinhadakatia.offering.repository.*;
import br.com.lojinhadakatia.shared.exception.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CandleOptionService {
    private final CandleOptionRepository options;
    private final ServiceOfferingRepository offerings;

    public CandleOptionService(CandleOptionRepository options, ServiceOfferingRepository offerings) {
        this.options = options; this.offerings = offerings;
    }

    private ServiceOffering candle(Long id, boolean publicOnly) {
        ServiceOffering offering = offerings.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vela não encontrada."));
        if (offering.getType() != OfferingType.CANDLE || (publicOnly && !offering.isPublished()))
            throw new ResourceNotFoundException("Vela não encontrada.");
        return offering;
    }

    public List<CandleOptionResponse> publicOptions(Long offeringId) {
        candle(offeringId, true);
        return options.findByOfferingIdAndActiveTrueOrderByKindAscValueAsc(offeringId)
            .stream().map(CandleOptionResponse::from).toList();
    }

    public List<CandleOptionResponse> adminOptions(Long offeringId) {
        candle(offeringId, false);
        return options.findByOfferingIdOrderByKindAscValueAsc(offeringId)
            .stream().map(CandleOptionResponse::from).toList();
    }

    @Transactional
    public List<CandleOptionResponse> replace(Long offeringId, List<CandleOptionRequest> requests) {
        ServiceOffering offering = candle(offeringId, false);
        Set<String> keys = new HashSet<>();
        for (CandleOptionRequest request : requests) {
            String normalized = request.value().trim();
            if (!keys.add(request.kind() + ":" + normalized.toLowerCase(Locale.ROOT)))
                throw new BusinessException("Opções de vela duplicadas.");
        }
        options.deleteAllInBatch(options.findByOfferingIdOrderByKindAscValueAsc(offeringId));
        options.flush();
        List<CandleOption> toSave = requests.stream()
            .map(r -> new CandleOption(offering, r.kind(), r.value().trim(), r.active())).toList();
        return options.saveAllAndFlush(toSave).stream().map(CandleOptionResponse::from).toList();
    }

    public String validateSelection(Long offeringId, Map<CandleOption.Kind, String> selections) {
        candle(offeringId, true);
        if (selections == null || selections.isEmpty()) return "";
        List<CandleOption> active = options.findByOfferingIdAndActiveTrueOrderByKindAscValueAsc(offeringId);
        List<String> confirmed = new ArrayList<>();
        for (var entry : selections.entrySet()) {
            String supplied = entry.getValue();
            if (entry.getKey() == null || supplied == null || supplied.isBlank()
                || active.stream().noneMatch(o -> o.getKind() == entry.getKey() && o.getValue().equals(supplied)))
                throw new BusinessException("Personalização de vela inválida.");
            confirmed.add(entry.getKey().name() + ": " + supplied);
        }
        return String.join("; ", confirmed);
    }
}
