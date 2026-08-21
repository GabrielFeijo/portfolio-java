package br.com.gabrielfeijo.portfolio.infrastructure.web.controller;

import br.com.gabrielfeijo.portfolio.application.dto.request.CreateCommandRequest;
import br.com.gabrielfeijo.portfolio.application.dto.request.PaginationQueryRequest;
import br.com.gabrielfeijo.portfolio.application.dto.request.UpdateCommandRequest;
import br.com.gabrielfeijo.portfolio.application.dto.response.CommandResponse;
import br.com.gabrielfeijo.portfolio.application.service.CommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v2/command")
@Tag(name = "Command", description = "Endpoints para gerenciamento de comandos do terminal virtual")
@RequiredArgsConstructor
public class CommandController {

    private final CommandService commandService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar novo comando (Requer API Key de Administrador)",
            security = @SecurityRequirement(name = "api-key"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Comando criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado - API Key inválida ou ausente"),
                    @ApiResponse(responseCode = "429", description = "Too Many Requests")
            }
    )
    public ResponseEntity<CommandResponse> createCommand(@Valid @RequestBody CreateCommandRequest request) {
        CommandResponse response = commandService.createCommand(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(
            summary = "Listar todos os comandos com suporte a busca e paginação",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de comandos retornada"),
                    @ApiResponse(responseCode = "429", description = "Too Many Requests")
            }
    )
    public ResponseEntity<List<CommandResponse>> getCommands(@ParameterObject @ModelAttribute PaginationQueryRequest query) {
        List<CommandResponse> commands = commandService.getCommands(query);
        return ResponseEntity.ok(commands);
    }

    @GetMapping("/{command}")
    @Operation(
            summary = "Buscar comando por nome ou alias",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Comando encontrado"),
                    @ApiResponse(responseCode = "400", description = "Nome do comando inválido"),
                    @ApiResponse(responseCode = "404", description = "Comando não encontrado"),
                    @ApiResponse(responseCode = "429", description = "Too Many Requests")
            }
    )
    public ResponseEntity<CommandResponse> getCommandByName(
            @Parameter(description = "Nome ou alias do comando", example = "skills")
            @PathVariable("command") String command) {
        CommandResponse response = commandService.getCommandByName(command);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar comando existente (Requer API Key de Administrador)",
            security = @SecurityRequirement(name = "api-key"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Comando atualizado"),
                    @ApiResponse(responseCode = "400", description = "ID ou dados inválidos"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado - API Key inválida ou ausente"),
                    @ApiResponse(responseCode = "404", description = "Comando não encontrado")
            }
    )
    public ResponseEntity<CommandResponse> updateCommand(
            @Parameter(description = "Identificador único do comando", example = "65d4f1a2e4b0a123456789ab")
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateCommandRequest request) {
        CommandResponse response = commandService.updateCommand(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Deletar comando (Requer API Key de Administrador)",
            security = @SecurityRequirement(name = "api-key"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Comando deletado"),
                    @ApiResponse(responseCode = "400", description = "ID inválido"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado - API Key inválida ou ausente"),
                    @ApiResponse(responseCode = "404", description = "Comando não encontrado")
            }
    )
    public ResponseEntity<Void> deleteCommand(
            @Parameter(description = "Identificador único do comando", example = "65d4f1a2e4b0a123456789ab")
            @PathVariable("id") String id) {
        commandService.deleteCommand(id);
        return ResponseEntity.noContent().build();
    }
}
