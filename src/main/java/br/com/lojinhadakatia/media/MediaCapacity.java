package br.com.lojinhadakatia.media;

import jakarta.persistence.*;

/** Singleton row serializes allocation and removal. */
@Entity @Table(name="media_capacity")
public class MediaCapacity {
    @Id private Integer id;
    protected MediaCapacity() {}
}
