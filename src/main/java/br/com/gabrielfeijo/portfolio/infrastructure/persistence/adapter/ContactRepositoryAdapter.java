package br.com.gabrielfeijo.portfolio.infrastructure.persistence.adapter;

import br.com.gabrielfeijo.portfolio.domain.model.Contact;
import br.com.gabrielfeijo.portfolio.domain.repository.ContactRepositoryPort;
import br.com.gabrielfeijo.portfolio.infrastructure.persistence.document.ContactDocument;
import br.com.gabrielfeijo.portfolio.infrastructure.persistence.mapper.ContactPersistenceMapper;
import br.com.gabrielfeijo.portfolio.infrastructure.persistence.repository.MongoContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ContactRepositoryAdapter implements ContactRepositoryPort {

    private final MongoContactRepository mongoContactRepository;
    private final ContactPersistenceMapper contactPersistenceMapper;

    @Override
    public Contact save(Contact contact) {
        ContactDocument document = contactPersistenceMapper.toDocument(contact);
        ContactDocument saved = mongoContactRepository.save(document);
        return contactPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Contact> findById(String id) {
        return mongoContactRepository.findById(id)
                .map(contactPersistenceMapper::toDomain);
    }
}
