package br.com.lojinhadakatia.media;

import br.com.lojinhadakatia.catalog.dto.ImageRequest;
import br.com.lojinhadakatia.shared.exception.BusinessException;
import java.net.URI;
import java.util.*;
import org.springframework.stereotype.Component;

/** Accept only stored media references, existing bundled catalog photos or external HTTPS images. */
@Component
public class ImageReferenceValidator {
    private final MediaService media;
    public ImageReferenceValidator(MediaService media){this.media=media;}

    public void validate(String value,boolean bundledAllowed){
        if(value==null||value.isBlank())return;
        if(value.length()>2048)throw new BusinessException("Endereço de foto muito longo.");
        if(MediaService.MANAGED.matcher(value).matches()){media.lockReference(value);return;}
        if(bundledAllowed&&value.matches("assets/images/catalogo/[a-z0-9-]+/[a-z0-9-]+\\.webp"))return;
        URI uri;
        try{uri=URI.create(value);}catch(Exception e){throw new BusinessException("URL de imagem inválida.");}
        if(!"https".equalsIgnoreCase(uri.getScheme())||uri.getHost()==null||uri.getUserInfo()!=null||
            uri.getPort()!=-1||uri.getFragment()!=null||uri.getPath().startsWith(MediaService.PREFIX))
            throw new BusinessException("Escolha uma foto da biblioteca ou uma URL HTTPS válida.");
    }
    public List<ImageRequest> ordered(List<ImageRequest> images,boolean bundledAllowed){
        if(images==null)return List.of();
        if(images.size()>12)throw new BusinessException("Limite de 12 fotos.");
        Set<String> urls=new HashSet<>();
        Set<Integer> positions=new HashSet<>();
        for(ImageRequest item:images){
            if(item==null||item.url()==null||item.url().isBlank()||item.position()<0||item.position()>=12||
                !urls.add(item.url())||!positions.add(item.position()))
                throw new BusinessException("As fotos devem ter endereços e posições diferentes.");
        }
        // Consistent lock order even if two administrators reorder the same photos.
        images.stream().map(ImageRequest::url).sorted().forEach(url->validate(url,bundledAllowed));
        return images.stream().sorted(Comparator.comparingInt(ImageRequest::position)).toList();
    }
}
