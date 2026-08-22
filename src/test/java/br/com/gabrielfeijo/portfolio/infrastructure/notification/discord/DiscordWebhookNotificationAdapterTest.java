package br.com.gabrielfeijo.portfolio.infrastructure.notification.discord;

import br.com.gabrielfeijo.portfolio.domain.model.Contact;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class DiscordWebhookNotificationAdapterTest {

    @Mock
    private RestClient restClient;

    @Test
    @DisplayName("Não deve chamar RestClient se a notificação do Discord estiver desabilitada")
    void shouldNotCallRestClientWhenDisabled() {
        DiscordWebhookNotificationAdapter adapter = new DiscordWebhookNotificationAdapter(
                restClient,
                "https://discord.com/api/webhooks/123/abc",
                false,
                "dev"
        );

        Contact contact = Contact.builder()
                .id("cnt-1")
                .name("Recrutador")
                .email("recrutador@empresa.com")
                .message("Olá")
                .createdAt(Instant.now())
                .build();

        assertDoesNotThrow(() -> adapter.sendContactNotification(contact));
        verifyNoInteractions(restClient);
    }

    @Test
    @DisplayName("Não deve lançar exceção mesmo se a URL do webhook for nula ou vazia")
    void shouldNotThrowWhenUrlIsEmpty() {
        DiscordWebhookNotificationAdapter adapter = new DiscordWebhookNotificationAdapter(
                "",
                true,
                "dev"
        );

        Contact contact = Contact.builder()
                .id("cnt-1")
                .name("Recrutador")
                .email("recrutador@empresa.com")
                .message("Olá")
                .createdAt(Instant.now())
                .build();

        assertDoesNotThrow(() -> adapter.sendContactNotification(contact));
    }
}
