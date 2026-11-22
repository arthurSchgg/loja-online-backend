package br.com.lojinhadakatia.whatsapp.service;

import jakarta.persistence.*;

@Entity @Table(name="whatsapp_message_templates")
public class MessageTemplate {
    @Id @Column(length=20) private String kind;
    @Column(nullable=false,length=1200) private String template;
    protected MessageTemplate(){}
}
