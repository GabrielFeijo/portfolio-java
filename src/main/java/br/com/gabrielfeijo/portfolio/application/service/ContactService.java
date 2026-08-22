package br.com.gabrielfeijo.portfolio.application.service;

import br.com.gabrielfeijo.portfolio.application.dto.request.CreateContactRequest;
import br.com.gabrielfeijo.portfolio.application.dto.response.ContactResponse;
import br.com.gabrielfeijo.portfolio.application.mapper.ContactMapper;
import br.com.gabrielfeijo.portfolio.domain.event.ContactCreatedEvent;
import br.com.gabrielfeijo.portfolio.domain.model.Contact;
import br.com.gabrielfeijo.portfolio.domain.repository.ContactRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepositoryPort contactRepositoryPort;
    private final ContactMapper contactMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ContactResponse createContact(CreateContactRequest request) {
        Contact contact = contactMapper.toDomain(request);
        contact.setName(request.name().trim());
        contact.setEmail(request.email().trim().toLowerCase());
        contact.setMessage(request.message().trim());

        Contact saved = contactRepositoryPort.save(contact);
        log.debug("Contact saved with ID: {}. Publishing event for notification.", saved.getId());
        eventPublisher.publishEvent(new ContactCreatedEvent(saved));

        return contactMapper.toResponse(saved);
    }
}
