package br.com.gabrielfeijo.portfolio.application.listener;

import br.com.gabrielfeijo.portfolio.domain.event.ContactCreatedEvent;
import br.com.gabrielfeijo.portfolio.domain.model.Contact;
import br.com.gabrielfeijo.portfolio.domain.port.NotificationPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ContactNotificationEventListenerTest {

    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private ContactNotificationEventListener listener;

    @Test
    @DisplayName("Deve repassar o contato recebido no evento para a porta de notificação")
    void shouldForwardContactToNotificationPort() {
        Contact contact = Contact.builder()
                .id("cnt-1")
                .name("Recrutador")
                .email("recrutador@empresa.com")
                .message("Excelente perfil!")
                .createdAt(Instant.now())
                .build();

        ContactCreatedEvent event = new ContactCreatedEvent(contact);

        listener.handleContactCreated(event);

        verify(notificationPort).sendContactNotification(contact);
    }
}
