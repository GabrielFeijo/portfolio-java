package br.com.gabrielfeijo.portfolio.infrastructure.web.controller;

import br.com.gabrielfeijo.portfolio.domain.repository.CommandRepositoryPort;
import br.com.gabrielfeijo.portfolio.domain.repository.ContactRepositoryPort;
import br.com.gabrielfeijo.portfolio.domain.repository.ReviewRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RootControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommandRepositoryPort commandRepositoryPort;

    @MockBean
    private ReviewRepositoryPort reviewRepositoryPort;

    @MockBean
    private ContactRepositoryPort contactRepositoryPort;

    @Test
    @DisplayName("GET /v2 - Deve retornar payload JSON estruturado de Health Check com status UP")
    void shouldReturnStructuredHealthPayload() throws Exception {
        mockMvc.perform(get("/v2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.application.name").value("api-portfolio-java"))
                .andExpect(jsonPath("$.application.version").value("2.0.0"))
                .andExpect(jsonPath("$.system.javaVersion").exists())
                .andExpect(jsonPath("$.uptime").exists());
    }

    @Test
    @DisplayName("GET /health - Deve retornar payload de saúde")
    void shouldReturnHealthOnRootHealth() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
