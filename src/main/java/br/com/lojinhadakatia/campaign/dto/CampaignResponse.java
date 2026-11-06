package br.com.lojinhadakatia.campaign.dto;
import java.time.Instant;
public record CampaignResponse(Long id,String title,String subtitle,String imageUrl,String linkUrl,
                               int position,boolean published,Instant startsAt,Instant endsAt) {}
