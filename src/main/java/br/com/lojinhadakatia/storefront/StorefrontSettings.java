package br.com.lojinhadakatia.storefront;

import jakarta.persistence.*;

@Entity @Table(name="storefront_settings")
public class StorefrontSettings {
    @Id private Integer id;
    @Column(name="featured_customized",nullable=false) private boolean featuredCustomized;
    protected StorefrontSettings(){}
}
