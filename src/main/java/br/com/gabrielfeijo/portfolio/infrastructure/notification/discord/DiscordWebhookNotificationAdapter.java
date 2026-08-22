package br.com.gabrielfeijo.portfolio.infrastructure.notification.discord;

import br.com.gabrielfeijo.portfolio.domain.model.Contact;
import br.com.gabrielfeijo.portfolio.domain.port.NotificationPort;
import br.com.gabrielfeijo.portfolio.infrastructure.notification.discord.dto.DiscordWebhookPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
public class DiscordWebhookNotificationAdapter implements NotificationPort {

    private final RestClient restClient;
    private final String webhookUrl;
    private final boolean enabled;
    private final String environment;

    @org.springframework.beans.factory.annotation.Autowired
    public DiscordWebhookNotificationAdapter(
            @Value("${portfolio.discord.webhook-url:}") String webhookUrl,
            @Value("${portfolio.discord.enabled:false}") boolean enabled,
            @Value("${spring.profiles.active:dev}") String environment) {
        this.webhookUrl = webhookUrl != null ? webhookUrl.trim() : "";
        this.enabled = enabled && !this.webhookUrl.isEmpty();
        this.environment = environment;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(4));

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    public DiscordWebhookNotificationAdapter(
            RestClient restClient,
            String webhookUrl,
            boolean enabled,
            String environment) {
        this.restClient = restClient;
        this.webhookUrl = webhookUrl != null ? webhookUrl.trim() : "";
        this.enabled = enabled && !this.webhookUrl.isEmpty();
        this.environment = environment;
    }

    @Override
    public void sendContactNotification(Contact contact) {
        if (!enabled) {
            log.debug("Discord webhook notification is disabled or URL is not configured. Skipping.");
            return;
        }

        try {
            DiscordWebhookPayload payload = buildPayload(contact);

            restClient.post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Discord notification sent successfully for contact from: {}", contact.getEmail());
        } catch (Exception e) {
            log.error("Failed to send Discord webhook notification for contact {}: {}", contact.getEmail(), e.getMessage());
        }
    }

    private DiscordWebhookPayload buildPayload(Contact contact) {
        int embedColorHex = 0x7127BA;

        List<DiscordWebhookPayload.EmbedField> fields = List.of(
                new DiscordWebhookPayload.EmbedField("👤 Nome", contact.getName(), true),
                new DiscordWebhookPayload.EmbedField("✉️ E-mail", contact.getEmail(), true),
                new DiscordWebhookPayload.EmbedField("🌐 Ambiente", "`" + environment.toUpperCase() + "`", true),
                new DiscordWebhookPayload.EmbedField("💬 Mensagem", "```\n" + truncate(contact.getMessage(), 1000) + "\n```", false)
        );

        DiscordWebhookPayload.EmbedFooter footer = new DiscordWebhookPayload.EmbedFooter(
                "VsCode-Portfolio • api-portfolio-java",
                "https://gabrielfeijo.com.br/favicon.ico"
        );

        DiscordWebhookPayload.DiscordEmbed embed = new DiscordWebhookPayload.DiscordEmbed(
                "📬 Novo Contato Recebido no Portfólio!",
                "Uma nova mensagem foi enviada através do formulário de contato em [gabrielfeijo.com.br](https://gabrielfeijo.com.br).",
                "https://gabrielfeijo.com.br",
                embedColorHex,
                fields,
                footer,
                Instant.now().toString()
        );

        return new DiscordWebhookPayload(
                "Portfolio Notifier",
                "https://gabrielfeijo.com.br/avatar.png",
                null,
                List.of(embed)
        );
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
}
