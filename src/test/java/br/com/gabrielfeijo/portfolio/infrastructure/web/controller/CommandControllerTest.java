package br.com.gabrielfeijo.portfolio.infrastructure.web.controller;

import br.com.gabrielfeijo.portfolio.application.dto.request.CreateCommandRequest;
import br.com.gabrielfeijo.portfolio.application.dto.response.CommandResponse;
import br.com.gabrielfeijo.portfolio.application.service.CommandService;
import br.com.gabrielfeijo.portfolio.domain.exception.ResourceNotFoundException;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommandService commandService;

    @MockBean
    private CommandRepositoryPort commandRepositoryPort;

    @MockBean
    private ReviewRepositoryPort reviewRepositoryPort;

    @MockBean
    private ContactRepositoryPort contactRepositoryPort;

    @Test
    @DisplayName("GET /v2/command - Deve listar comandos publicamente")
    void shouldListCommandsPublicly() throws Exception {
        CommandResponse cmd = new CommandResponse(
                "cmd-1", "skills", List.of("stack"), "portfolio", "desc", "all", List.of("Java"),
                Instant.now(), Instant.now()
        );
        when(commandService.getCommands(any())).thenReturn(List.of(cmd));

        mockMvc.perform(get("/v2/command"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].command").value("skills"));
    }

    @Test
    @DisplayName("GET /v2/command/{command} - Deve buscar comando por nome")
    void shouldGetCommandByName() throws Exception {
        CommandResponse cmd = new CommandResponse(
                "cmd-1", "skills", List.of("stack"), "portfolio", "desc", "all", List.of("Java"),
                Instant.now(), Instant.now()
        );
        when(commandService.getCommandByName("skills")).thenReturn(cmd);

        mockMvc.perform(get("/v2/command/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.command").value("skills"));
    }

    @Test
    @DisplayName("GET /v2/command/{command} - Deve retornar 404 quando não encontrar")
    void shouldReturn404WhenCommandNotFound() throws Exception {
        when(commandService.getCommandByName("unknown"))
                .thenThrow(new ResourceNotFoundException("Command 'unknown' not found"));

        mockMvc.perform(get("/v2/command/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @DisplayName("POST /v2/command - Deve retornar 401 Unauthorized quando não enviar API Key")
    void shouldReturn401WhenCreatingWithoutApiKey() throws Exception {
        CreateCommandRequest request = new CreateCommandRequest(
                "skills", List.of("stack"), "general", "desc", "all", List.of("Java")
        );

        mockMvc.perform(post("/v2/command")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").value(401));
    }

    @Test
    @DisplayName("POST /v2/command - Deve criar comando com API Key válida no header x-api-key")
    void shouldCreateCommandWithValidApiKey() throws Exception {
        CreateCommandRequest request = new CreateCommandRequest(
                "skills", List.of("stack"), "general", "desc", "all", List.of("Java")
        );
        CommandResponse cmd = new CommandResponse(
                "cmd-1", "skills", List.of("stack"), "general", "desc", "all", List.of("Java"),
                Instant.now(), Instant.now()
        );
        when(commandService.createCommand(any())).thenReturn(cmd);

        mockMvc.perform(post("/v2/command")
                        .header("x-api-key", "test-admin-secret-key-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.command").value("skills"));
    }

    @Test
    @DisplayName("DELETE /v2/command/{id} - Deve deletar comando com API Key")
    void shouldDeleteCommandWithApiKey() throws Exception {
        doNothing().when(commandService).deleteCommand("cmd-1");

        mockMvc.perform(delete("/v2/command/cmd-1")
                        .header("x-api-key", "test-admin-secret-key-123"))
                .andExpect(status().isNoContent());
    }
}
