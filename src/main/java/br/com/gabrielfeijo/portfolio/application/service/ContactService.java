package br.com.gabrielfeijo.portfolio.application.service;

import br.com.gabrielfeijo.portfolio.application.dto.request.CreateContactRequest;
import br.com.gabrielfeijo.portfolio.application.dto.response.ContactResponse;
import br.com.gabrielfeijo.portfolio.application.mapper.ContactMapper;
import br.com.gabrielfeijo.portfolio.domain.model.Contact;
import br.com.gabrielfeijo.portfolio.domain.repository.ContactRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepositoryPort contactRepositoryPort;
    private final ContactMapper contactMapper;

    @Transactional
    public ContactResponse createContact(CreateContactRequest request) {
        Contact contact = contactMapper.toDomain(request);
        contact.setName(request.name().trim());
        contact.setEmail(request.email().trim().toLowerCase());
        contact.setMessage(request.message().trim());

        Contact saved = contactRepositoryPort.save(contact);
        return contactMapper.toResponse(saved);
    }
}
