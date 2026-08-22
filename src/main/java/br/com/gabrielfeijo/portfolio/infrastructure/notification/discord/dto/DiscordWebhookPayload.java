package br.com.gabrielfeijo.portfolio.infrastructure.notification.discord.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DiscordWebhookPayload(
        String username,
        @JsonProperty("avatar_url") String avatarUrl,
        String content,
        List<DiscordEmbed> embeds
) {
    public record DiscordEmbed(
            String title,
            String description,
            String url,
            Integer color,
            List<EmbedField> fields,
            EmbedFooter footer,
            String timestamp
    ) {}

    public record EmbedField(
            String name,
            String value,
            Boolean inline
    ) {}

    public record EmbedFooter(
            String text,
            @JsonProperty("icon_url") String iconUrl
    ) {}
}
