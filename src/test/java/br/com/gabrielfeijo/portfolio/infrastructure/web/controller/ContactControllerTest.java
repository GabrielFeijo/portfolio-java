package br.com.gabrielfeijo.portfolio.infrastructure.web.controller;

import br.com.gabrielfeijo.portfolio.application.dto.request.CreateContactRequest;
import br.com.gabrielfeijo.portfolio.application.dto.response.ContactResponse;
import br.com.gabrielfeijo.portfolio.application.service.ContactService;
import br.com.gabrielfeijo.portfolio.domain.repository.CommandRepositoryPort;
import br.com.gabrielfeijo.portfolio.domain.repository.ContactRepositoryPort;
import br.com.gabrielfeijo.portfolio.domain.repository.ReviewRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ContactService contactService;

    @MockBean
    private CommandRepositoryPort commandRepositoryPort;

    @MockBean
    private ReviewRepositoryPort reviewRepositoryPort;

    @MockBean
    private ContactRepositoryPort contactRepositoryPort;

    @Test
    @DisplayName("POST /v2/contact - Deve registrar mensagem de contato com sucesso")
    void shouldRegisterContactMessage() throws Exception {
        CreateContactRequest request = new CreateContactRequest(
                "Gabriel Feijó",
                "gabriel@email.com",
                "Gostaria de conversar sobre uma oportunidade de trabalho."
        );
        ContactResponse response = new ContactResponse(
                "cnt-1", "Gabriel Feijó", "gabriel@email.com",
                "Gostaria de conversar sobre uma oportunidade de trabalho.",
                Instant.now(), Instant.now()
        );
        when(contactService.createContact(any())).thenReturn(response);

        mockMvc.perform(post("/v2/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Gabriel Feijó"))
                .andExpect(jsonPath("$.email").value("gabriel@email.com"));
    }

    @Test
    @DisplayName("POST /v2/contact - Deve retornar 400 Bad Request se e-mail for inválido")
    void shouldReturn400WhenEmailInvalid() throws Exception {
        CreateContactRequest request = new CreateContactRequest(
                "Gabriel Feijó",
                "invalid-email",
                "Gostaria de conversar sobre uma oportunidade de trabalho."
        );

        mockMvc.perform(post("/v2/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400));
    }
}
