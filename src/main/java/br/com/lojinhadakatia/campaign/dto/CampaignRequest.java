package br.com.lojinhadakatia.campaign.dto;
import jakarta.validation.constraints.*;
import java.time.Instant;
/** Dados de banner de campanha sem regra de desconto automático. */
public record CampaignRequest(
 @NotBlank @Size(max=180) String title,
 @Size(max=600) String subtitle,
 @Size(max=2048) String imageUrl,
 @Size(max=2048) String linkUrl,
 @Min(0) int position,
 boolean published,
 Instant startsAt,
 Instant endsAt
) {}
