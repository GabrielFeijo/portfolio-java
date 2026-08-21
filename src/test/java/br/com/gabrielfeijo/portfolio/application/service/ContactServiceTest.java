package br.com.gabrielfeijo.portfolio.application.service;

import br.com.gabrielfeijo.portfolio.application.dto.request.CreateContactRequest;
import br.com.gabrielfeijo.portfolio.application.dto.response.ContactResponse;
import br.com.gabrielfeijo.portfolio.application.mapper.ContactMapper;
import br.com.gabrielfeijo.portfolio.domain.model.Contact;
import br.com.gabrielfeijo.portfolio.domain.repository.ContactRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepositoryPort contactRepositoryPort;

    @Spy
    private ContactMapper contactMapper = Mappers.getMapper(ContactMapper.class);

    @InjectMocks
    private ContactService contactService;

    @Test
    @DisplayName("Deve registrar mensagem de contato com sucesso e sanitizar email")
    void shouldCreateContactSuccessfully() {
        CreateContactRequest request = new CreateContactRequest(
                "  Gabriel Feijó  ",
                "  GABRIEL@EMAIL.COM  ",
                "  Olá, gostaria de conversar sobre uma oportunidade de projeto.  "
        );

        Contact savedContact = Contact.builder()
                .id("cnt-123")
                .name("Gabriel Feijó")
                .email("gabriel@email.com")
                .message("Olá, gostaria de conversar sobre uma oportunidade de projeto.")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(contactRepositoryPort.save(any(Contact.class))).thenReturn(savedContact);

        ContactResponse response = contactService.createContact(request);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Gabriel Feijó");
        assertThat(response.email()).isEqualTo("gabriel@email.com");
        verify(contactRepositoryPort).save(any(Contact.class));
    }
}
