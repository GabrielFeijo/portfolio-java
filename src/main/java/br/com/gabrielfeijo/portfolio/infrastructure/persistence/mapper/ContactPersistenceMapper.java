package br.com.gabrielfeijo.portfolio.infrastructure.persistence.mapper;

import br.com.gabrielfeijo.portfolio.domain.model.Contact;
import br.com.gabrielfeijo.portfolio.infrastructure.persistence.document.ContactDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContactPersistenceMapper {
    Contact toDomain(ContactDocument document);
    ContactDocument toDocument(Contact domain);
}
