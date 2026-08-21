package br.com.gabrielfeijo.portfolio.application.mapper;

import br.com.gabrielfeijo.portfolio.application.dto.request.CreateContactRequest;
import br.com.gabrielfeijo.portfolio.application.dto.response.ContactResponse;
import br.com.gabrielfeijo.portfolio.domain.model.Contact;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContactMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Contact toDomain(CreateContactRequest request);

    ContactResponse toResponse(Contact contact);
}
