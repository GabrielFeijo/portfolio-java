package br.com.gabrielfeijo.portfolio.application.service;

import br.com.gabrielfeijo.portfolio.application.dto.request.CreateCommandRequest;
import br.com.gabrielfeijo.portfolio.application.dto.request.PaginationQueryRequest;
import br.com.gabrielfeijo.portfolio.application.dto.response.CommandResponse;
import br.com.gabrielfeijo.portfolio.application.mapper.CommandMapper;
import br.com.gabrielfeijo.portfolio.domain.exception.BusinessValidationException;
import br.com.gabrielfeijo.portfolio.domain.exception.DuplicateResourceException;
import br.com.gabrielfeijo.portfolio.domain.exception.ResourceNotFoundException;
import br.com.gabrielfeijo.portfolio.domain.model.Command;
import br.com.gabrielfeijo.portfolio.domain.repository.CommandRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandServiceTest {

    @Mock
    private CommandRepositoryPort commandRepositoryPort;

    @Spy
    private CommandMapper commandMapper = Mappers.getMapper(CommandMapper.class);

    @InjectMocks
    private CommandService commandService;

    private Command sampleCommand;

    @BeforeEach
    void setUp() {
        sampleCommand = Command.builder()
                .id("cmd-123")
                .command("skills")
                .aliases(List.of("habilidades", "stack"))
                .category("portfolio")
                .description("Lista habilidades")
                .language("all")
                .response(List.of("Java", "Spring Boot"))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar um comando com sucesso e sanitizar o nome")
    void shouldCreateCommandSuccessfully() {
        CreateCommandRequest request = new CreateCommandRequest(
                " Skills ",
                List.of(" Habilidades "),
                "portfolio",
                "Descrição",
                "pt",
                List.of("Java 21"));

        when(commandRepositoryPort.existsByCommand("skills")).thenReturn(false);
        when(commandRepositoryPort.save(any(Command.class))).thenReturn(sampleCommand);

        CommandResponse response = commandService.createCommand(request);

        assertThat(response).isNotNull();
        assertThat(response.command()).isEqualTo("skills");
        verify(commandRepositoryPort).save(any(Command.class));
    }

    @Test
    @DisplayName("Deve lançar DuplicateResourceException se comando já existir")
    void shouldThrowDuplicateResourceExceptionWhenCommandExists() {
        CreateCommandRequest request = new CreateCommandRequest(
                "skills",
                List.of("stack"),
                "portfolio",
                "Descrição",
                "all",
                List.of("Java 21"));

        when(commandRepositoryPort.existsByCommand("skills")).thenReturn(true);

        assertThatThrownBy(() -> commandService.createCommand(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("skills");
    }

    @Test
    @DisplayName("Deve buscar comando por nome com sanitização NFD")
    void shouldGetCommandByNameWithNfdSanitization() {
        when(commandRepositoryPort.findByCommandOrAlias("skills")).thenReturn(Optional.of(sampleCommand));

        CommandResponse response = commandService.getCommandByName("  SKILLS  ");

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("cmd-123");
    }

    @Test
    @DisplayName("Deve lançar BusinessValidationException se nome do comando for menor que 2 caracteres")
    void shouldThrowBusinessValidationExceptionWhenCommandNameTooShort() {
        assertThatThrownBy(() -> commandService.getCommandByName("a"))
                .isInstanceOf(BusinessValidationException.class);
    }

    @Test
    @DisplayName("Deve lançar BusinessValidationException se nome do comando for maior que 100 caracteres")
    void shouldThrowBusinessValidationExceptionWhenCommandNameTooLong() {
        String longName = "a".repeat(101);
        assertThatThrownBy(() -> commandService.getCommandByName(longName))
                .isInstanceOf(BusinessValidationException.class);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException se comando não for encontrado")
    void shouldThrowNotFoundWhenCommandDoesNotExist() {
        when(commandRepositoryPort.findByCommandOrAlias("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commandService.getCommandByName("unknown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("unknown");
    }

    @Test
    @DisplayName("Deve deletar comando por ID com sucesso")
    void shouldDeleteCommandSuccessfully() {
        when(commandRepositoryPort.findById("cmd-123")).thenReturn(Optional.of(sampleCommand));

        commandService.deleteCommand("cmd-123");

        verify(commandRepositoryPort).deleteById("cmd-123");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao deletar ID inexistente")
    void shouldThrowNotFoundWhenDeletingNonExistentCommand() {
        when(commandRepositoryPort.findById("non-existent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commandService.deleteCommand("non-existent"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("non-existent");
    }

    @Test
    @DisplayName("Deve listar comandos com paginação")
    void shouldListCommands() {
        PaginationQueryRequest query = new PaginationQueryRequest(false, 1, 10, "skill");
        when(commandRepositoryPort.findAll(eq("skill"), eq(false), eq(1), eq(10)))
                .thenReturn(List.of(sampleCommand));

        List<CommandResponse> responses = commandService.getCommands(query);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).command()).isEqualTo("skills");
    }

    @ParameterizedTest(name = "Sanitização: ''{0}'' → ''{1}''")
    @DisplayName("Deve sanitizar o nome do comando corretamente via createCommand")
    @CsvSource({
            " Skills ,         skills",
            "HELLO WORLD,      hello world",
            "café,             cafe",
            "git-log,          git-log",
            "ls --all,         ls --all",
            "cmd@#$%,          cmd",
    })
    void shouldSanitizeCommandNameViaCreateCommand(String rawInput, String expectedSanitized) {
        String expected = expectedSanitized.trim();
        when(commandRepositoryPort.existsByCommand(expected)).thenReturn(false);
        when(commandRepositoryPort.save(any(Command.class))).thenAnswer(inv -> {
            Command c = inv.getArgument(0);
            assertThat(c.getCommand()).isEqualTo(expected);
            return sampleCommand;
        });

        commandService.createCommand(new CreateCommandRequest(
                rawInput.trim(), List.of(), "general", "", "all", List.of()));
    }
}

