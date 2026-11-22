package br.com.lojinhadakatia.media;

import br.com.lojinhadakatia.shared.exception.BusinessException;
import jakarta.validation.constraints.*;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
public class MediaController {
    private final MediaService service;
    private final PhotoCodec codec;
    public MediaController(MediaService service,PhotoCodec codec){this.service=service;this.codec=codec;}
    @PostMapping(path="/api/v1/admin/media",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaService.Info> upload(@RequestParam("file") MultipartFile file){
        if(file.isEmpty()||file.getSize()>PhotoCodec.MAX_INPUT)throw new BusinessException("Selecione uma foto de até 8 MB.");
        try{ return ResponseEntity.status(HttpStatus.CREATED).body(service.upload(codec.sanitize(file.getBytes()))); }
        catch(IOException error){throw new BusinessException("Não foi possível receber a foto.");}
    }
    @GetMapping("/api/v1/admin/media")
    public MediaService.Page list(@RequestParam(defaultValue="0") @Min(0) @Max(10000) int page,
                                  @RequestParam(defaultValue="24") @Min(1) @Max(50) int size){return service.list(page,size);}
    @DeleteMapping("/api/v1/admin/media/{id}")
    public ResponseEntity<Void> remove(@PathVariable UUID id){service.delete(id);return ResponseEntity.noContent().build();}
    @GetMapping("/api/v1/admin/media/{id}")
    public ResponseEntity<byte[]> privateImage(@PathVariable UUID id){
        var meta=service.metadata(id,false);
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).contentType(MediaType.parseMediaType(meta.contentType()))
            .contentLength(meta.byteLength()).body(service.bytes(id,false));
    }
    @GetMapping("/api/v1/media/{id}")
    public ResponseEntity<byte[]> publicImage(@PathVariable UUID id,WebRequest request){
        var meta=service.metadata(id,true);
        String tag="\""+meta.digest()+"\"";
        var cache=CacheControl.maxAge(Duration.ofHours(1)).cachePublic().mustRevalidate();
        if(request.checkNotModified(tag))return ResponseEntity.status(HttpStatus.NOT_MODIFIED).cacheControl(cache).eTag(tag).build();
        return ResponseEntity.ok().cacheControl(cache).eTag(tag).contentType(MediaType.parseMediaType(meta.contentType()))
            .contentLength(meta.byteLength()).body(service.bytes(id,true));
    }
}
