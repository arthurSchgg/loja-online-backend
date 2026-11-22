package br.com.lojinhadakatia.media;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/** Schema mapping only. Reads deliberately project metadata without loading the blob. */
@Entity @Table(name="media_assets")
public class MediaAsset {
    @Id private UUID id;
    @Column(nullable=false,length=64,unique=true) private String sha256;
    @Column(name="content_type",nullable=false,length=20) private String contentType;
    @Column(name="byte_length",nullable=false) private int byteLength;
    @Column(nullable=false) private int width;
    @Column(nullable=false) private int height;
    @Column(nullable=false,columnDefinition="bytea") private byte[] payload;
    @Column(name="created_at",nullable=false) private Instant createdAt;
    protected MediaAsset() {}
}
