package br.com.lojinhadakatia.store.service;

import br.com.lojinhadakatia.store.dto.*;
import br.com.lojinhadakatia.store.entity.StoreInformation;
import br.com.lojinhadakatia.store.repository.StoreInformationRepository;
import br.com.lojinhadakatia.shared.exception.BusinessException;
import java.net.URI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Consulta e atualização das informações institucionais da loja. */
@Service @Transactional(readOnly=true)
public class StoreService {
    private final StoreInformationRepository repository;
    public StoreService(StoreInformationRepository repository){this.repository=repository;}
    public StoreResponse publicInfo(){
        return repository.findById(1L).map(this::map)
            .orElse(new StoreResponse("Lojinha da Kátia","Mais do que comprar, é viver uma experiência!",null,null,null,null));
    }
    @Transactional
    public StoreResponse update(StoreRequest request){
        validateInstagram(request.instagramUrl());
        StoreInformation info=repository.findById(1L).orElseGet(
            ()->new StoreInformation(request.displayName().trim(),request.slogan(),request.description(),
                                      request.whatsapp(),request.instagramUrl(),request.email()));
        info.update(request.displayName().trim(),request.slogan(),request.description(),request.whatsapp(),request.instagramUrl(),request.email());
        return map(repository.save(info));
    }
    private void validateInstagram(String url){
        if(url==null || url.isBlank())return;
        URI parsed;
        try{parsed=URI.create(url);}catch(Exception exception){throw new BusinessException("URL de rede social inválida.");}
        if(!"https".equalsIgnoreCase(parsed.getScheme()) || parsed.getHost()==null ||
           !(parsed.getHost().equalsIgnoreCase("instagram.com")||parsed.getHost().equalsIgnoreCase("www.instagram.com")))
            throw new BusinessException("Informe uma URL HTTPS válida do Instagram.");
    }
    private StoreResponse map(StoreInformation info){
        return new StoreResponse(info.getDisplayName(),info.getSlogan(),info.getDescription(),
                                 info.getWhatsapp(),info.getInstagramUrl(),info.getEmail());
    }
}
