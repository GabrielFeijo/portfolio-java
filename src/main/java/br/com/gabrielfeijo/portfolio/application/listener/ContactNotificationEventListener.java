package br.com.gabrielfeijo.portfolio.application.listener;

import br.com.gabrielfeijo.portfolio.domain.event.ContactCreatedEvent;
import br.com.gabrielfeijo.portfolio.domain.port.NotificationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContactNotificationEventListener {

    private final NotificationPort notificationPort;

    @Async
    @EventListener
    public void handleContactCreated(ContactCreatedEvent event) {
        log.debug("Received ContactCreatedEvent for contact ID: {}", event.contact().getId());
        notificationPort.sendContactNotification(event.contact());
    }
}
