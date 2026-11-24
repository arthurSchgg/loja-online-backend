package br.com.lojinhadakatia.media;

import br.com.lojinhadakatia.shared.exception.*;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** Bounded persistent photos in the existing database; no local filesystem or new provider. */
@Service
@Transactional(readOnly=true)
public class MediaService {
    public static final long LIMIT_BYTES=256L*1024*1024;
    public static final String PREFIX="/api/v1/media/";
    public static final Pattern MANAGED=Pattern.compile("^/api/v1/media/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
    private static final String COLUMNS="id,sha256,content_type,byte_length,width,height,created_at";
    private final JdbcTemplate jdbc;
    public MediaService(JdbcTemplate jdbc){this.jdbc=jdbc;}

    public record Info(UUID id,String url,String contentType,int byteLength,int width,int height,
                       Instant createdAt,boolean used){}
    public record Page(List<Info> content,boolean last,long usedBytes,long capacityBytes){}
    public record Metadata(UUID id,String digest,String contentType,int byteLength){}
    private record Raw(UUID id,String digest,String type,int length,int width,int height,Instant created){}

    private List<Raw> rows(String sql,Object... args){
        return jdbc.query(sql,(rs,n)->new Raw(rs.getObject("id",UUID.class),rs.getString("sha256"),
            rs.getString("content_type"),rs.getInt("byte_length"),rs.getInt("width"),rs.getInt("height"),
            rs.getTimestamp("created_at").toInstant()),args);
    }
    private String url(UUID id){return PREFIX+id;}
    private Info info(Raw r){return new Info(r.id,url(r.id),r.type,r.length,r.width,r.height,r.created,referenced(r.id,false));}
    private long used(){return Optional.ofNullable(jdbc.queryForObject("SELECT COALESCE(SUM(byte_length),0) FROM media_assets",Long.class)).orElse(0L);}
    private void lockCapacity(){
        if(jdbc.query("SELECT id FROM media_capacity WHERE id=1 FOR UPDATE",(rs,n)->rs.getInt(1)).isEmpty())
            throw new BusinessException("Biblioteca de fotos indisponível. Tente novamente.");
    }
    private Raw find(UUID id,boolean lock){
        return rows("SELECT "+COLUMNS+" FROM media_assets WHERE id=?"+(lock?" FOR UPDATE":""),id).stream()
            .findFirst().orElseThrow(()->new ResourceNotFoundException("Foto não encontrada."));
    }

    @Transactional
    public Info upload(PhotoCodec.Photo photo){
        String digest;
        try{digest=HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(photo.bytes()));}
        catch(java.security.NoSuchAlgorithmException impossible){throw new IllegalStateException(impossible);}
        lockCapacity();
        var old=rows("SELECT "+COLUMNS+" FROM media_assets WHERE sha256=?",digest);
        if(!old.isEmpty())return info(old.getFirst());
        if(used()+photo.bytes().length>LIMIT_BYTES)
            throw new BusinessException("Biblioteca de fotos cheia. Remova fotos sem uso antes de enviar outras.");
        UUID id=UUID.randomUUID();
        jdbc.update("INSERT INTO media_assets(id,sha256,content_type,byte_length,width,height,payload,created_at) VALUES(?,?,?,?,?,?,?,?)",
            id,digest,photo.contentType(),photo.bytes().length,photo.width(),photo.height(),photo.bytes(),Timestamp.from(Instant.now()));
        return info(find(id,false));
    }

    public Page list(int page,int size){
        var list=rows("SELECT "+COLUMNS+" FROM media_assets ORDER BY created_at DESC,id DESC LIMIT ? OFFSET ?",
            size+1,Math.multiplyExact(page,size));
        var visible=list.stream().limit(size).toList();
        Set<String> usedUrls=new HashSet<>();
        if(!visible.isEmpty()){
            String placeholders=String.join(",",Collections.nCopies(visible.size(),"?"));
            Object[] urls=visible.stream().map(r->url(r.id)).toArray();
            usedUrls.addAll(jdbc.queryForList("""
                SELECT image_url FROM (
                    SELECT image_url FROM product_images UNION
                    SELECT image_url FROM service_offerings UNION
                    SELECT image_url FROM offering_images UNION
                    SELECT image_url FROM content_sections UNION
                    SELECT image_url FROM campaigns
                ) refs WHERE image_url IN (%s)
                """.formatted(placeholders),String.class,urls));
        }
        return new Page(visible.stream().map(r->new Info(r.id,url(r.id),r.type,r.length,r.width,r.height,r.created,
            usedUrls.contains(url(r.id)))).toList(),list.size()<=size,used(),LIMIT_BYTES);
    }

    @Transactional
    public void delete(UUID id){
        lockCapacity();find(id,true);
        if(referenced(id,false))throw new BusinessException("Esta foto ainda está vinculada a um cadastro. Remova o vínculo antes de excluir.");
        jdbc.update("DELETE FROM media_assets WHERE id=?",id);
    }

    /** Domain saves hold the same row lock as removal until their transaction commits. */
    @Transactional(propagation=Propagation.MANDATORY)
    public void lockReference(String path){
        if(!MANAGED.matcher(path).matches())throw new BusinessException("Referência de foto inválida.");
        try{find(UUID.fromString(path.substring(PREFIX.length())),true);}
        catch(ResourceNotFoundException missing){throw new BusinessException("A foto não está mais na biblioteca. Selecione outra.");}
    }

    public Metadata metadata(UUID id,boolean publicRead){
        var r=find(id,false);
        if(publicRead&&!referenced(id,true))throw new ResourceNotFoundException("Foto não publicada.");
        return new Metadata(r.id,r.digest,r.type,r.length);
    }

    public byte[] bytes(UUID id,boolean publicRead){
        if(publicRead&&!referenced(id,true))throw new ResourceNotFoundException("Foto não publicada.");
        var found=jdbc.query("SELECT payload FROM media_assets WHERE id=?",(rs,n)->rs.getBytes(1),id);
        if(found.isEmpty())throw new ResourceNotFoundException("Foto não encontrada.");
        return found.getFirst();
    }

    private boolean referenced(UUID id,boolean publicRead){
        String path=url(id);
        String p=publicRead?" AND p.published=TRUE":"";
        String o=publicRead?" AND o.published=TRUE":"";
        String c=publicRead?" AND published=TRUE":"";
        String campaign=publicRead?c+" AND (starts_at IS NULL OR starts_at<=CURRENT_TIMESTAMP) AND (ends_at IS NULL OR ends_at>CURRENT_TIMESTAMP)":"";
        return Boolean.TRUE.equals(jdbc.queryForObject("""
            SELECT EXISTS (
                SELECT 1 FROM product_images pi JOIN products p ON p.id=pi.product_id WHERE pi.image_url=? %s
                UNION ALL SELECT 1 FROM service_offerings o WHERE o.image_url=? %s
                UNION ALL SELECT 1 FROM offering_images oi JOIN service_offerings o ON o.id=oi.offering_id WHERE oi.image_url=? %s
                UNION ALL SELECT 1 FROM content_sections WHERE image_url=? %s
                UNION ALL SELECT 1 FROM campaigns WHERE image_url=? %s
            )
            """.formatted(p,o,o,c,campaign),Boolean.class,path,path,path,path,path));
    }
}
