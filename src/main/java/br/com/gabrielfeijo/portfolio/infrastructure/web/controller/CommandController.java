package br.com.gabrielfeijo.portfolio.infrastructure.web.controller;

import br.com.gabrielfeijo.portfolio.application.dto.request.CreateCommandRequest;
import br.com.gabrielfeijo.portfolio.application.dto.request.PaginationQueryRequest;
import br.com.gabrielfeijo.portfolio.application.dto.request.UpdateCommandRequest;
import br.com.gabrielfeijo.portfolio.application.dto.response.CommandResponse;
import br.com.gabrielfeijo.portfolio.application.dto.response.ErrorResponse;
import br.com.gabrielfeijo.portfolio.application.service.CommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v2/command")
@Tag(name = "Command", description = "Endpoints para gerenciamento e consulta de comandos do terminal virtual")
@RequiredArgsConstructor
public class CommandController {

    private final CommandService commandService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar novo comando (Requer API Key de Administrador)",
            description = "Registra um novo comando executável no terminal interativo do portfólio. Requer autenticação via header 'x-api-key'.",
            security = @SecurityRequirement(name = "api-key"),
            requestBody = @RequestBody(
                    description = "Dados do comando a ser criado",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateCommandRequest.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de Criação de Comando",
                                    value = """
                                            {
                                              "command": "skills",
                                              "aliases": ["habilidades", "stack", "techs"],
                                              "category": "portfolio",
                                              "description": "Lista as principais tecnologias e habilidades do desenvolvedor",
                                              "language": "pt",
                                              "response": [
                                                "☕ Java 21 & Spring Boot 3.3 (Clean Architecture, Virtual Threads)",
                                                "🍃 Spring Data MongoDB & PostgreSQL",
                                                "🔒 Spring Security 6 & API Key Auth",
                                                "⚡ Bucket4j Rate Limiting & OpenAPI 3 (Swagger)",
                                                "🐳 Docker & Docker Compose"
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Comando criado com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CommandResponse.class),
                                    examples = @ExampleObject(
                                            name = "Comando Criado",
                                            value = """
                                                    {
                                                      "id": "6a88e4dfd7a2b0617a6db3fa",
                                                      "command": "skills",
                                                      "aliases": ["habilidades", "stack", "techs"],
                                                      "category": "portfolio",
                                                      "description": "Lista as principais tecnologias e habilidades do desenvolvedor",
                                                      "language": "pt",
                                                      "response": [
                                                        "☕ Java 21 & Spring Boot 3.3 (Clean Architecture, Virtual Threads)",
                                                        "🍃 Spring Data MongoDB & PostgreSQL",
                                                        "🔒 Spring Security 6 & API Key Auth",
                                                        "⚡ Bucket4j Rate Limiting & OpenAPI 3 (Swagger)",
                                                        "🐳 Docker & Docker Compose"
                                                      ],
                                                      "createdAt": "2026-08-21T23:53:03.134Z",
                                                      "updatedAt": "2026-08-21T23:53:03.134Z"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Dados inválidos ou comando duplicado",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "Erro de Validação",
                                            value = """
                                                    {
                                                      "statusCode": 400,
                                                      "timestamp": "2026-08-21T23:53:03.499Z",
                                                      "path": "/v2/command",
                                                      "method": "POST",
                                                      "message": "Command 'skills' already exists",
                                                      "error": "Bad Request"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Não autorizado - API Key inválida ou ausente",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "Não Autorizado",
                                            value = """
                                                    {
                                                      "statusCode": 401,
                                                      "timestamp": "2026-08-21T23:53:02.998Z",
                                                      "path": "/v2/command",
                                                      "method": "POST",
                                                      "message": "Não autorizado - API Key inválida ou ausente",
                                                      "error": "Unauthorized"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "429",
                            description = "Limite de taxa excedido (Rate Limit)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<CommandResponse> createCommand(@Valid @org.springframework.web.bind.annotation.RequestBody CreateCommandRequest request) {
        CommandResponse response = commandService.createCommand(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(
            summary = "Listar todos os comandos com busca e paginação",
            description = "Retorna o catálogo de comandos disponíveis no terminal com suporte a filtro textual em nome, aliases, categoria ou descrição.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de comandos retornada com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = CommandResponse.class)),
                                    examples = @ExampleObject(
                                            name = "Lista de Comandos",
                                            value = """
                                                    [
                                                      {
                                                        "id": "6a88e4dfd7a2b0617a6db3fa",
                                                        "command": "skills",
                                                        "aliases": ["habilidades", "stack", "techs"],
                                                        "category": "portfolio",
                                                        "description": "Lista as principais tecnologias e habilidades do desenvolvedor",
                                                        "language": "pt",
                                                        "response": [
                                                          "☕ Java 21 & Spring Boot 3.3 (Clean Architecture, Virtual Threads)",
                                                          "🍃 Spring Data MongoDB & PostgreSQL"
                                                        ],
                                                        "createdAt": "2026-08-21T23:53:03.134Z",
                                                        "updatedAt": "2026-08-21T23:53:03.134Z"
                                                      }
                                                    ]
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "429",
                            description = "Limite de taxa excedido (Rate Limit)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<List<CommandResponse>> getCommands(@ParameterObject @ModelAttribute PaginationQueryRequest query) {
        List<CommandResponse> commands = commandService.getCommands(query);
        return ResponseEntity.ok(commands);
    }

    @GetMapping("/{command}")
    @Operation(
            summary = "Buscar comando por nome ou alias",
            description = "Realiza busca exata ou por aliases do comando com sanitização automática de caracteres e normalização NFD.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comando localizado com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CommandResponse.class),
                                    examples = @ExampleObject(
                                            name = "Comando Encontrado",
                                            value = """
                                                    {
                                                      "id": "6a88e4dfd7a2b0617a6db3fa",
                                                      "command": "skills",
                                                      "aliases": ["habilidades", "stack", "techs"],
                                                      "category": "portfolio",
                                                      "description": "Lista as principais tecnologias e habilidades do desenvolvedor",
                                                      "language": "pt",
                                                      "response": [
                                                        "☕ Java 21 & Spring Boot 3.3 (Clean Architecture, Virtual Threads)",
                                                        "🍃 Spring Data MongoDB & PostgreSQL"
                                                      ],
                                                      "createdAt": "2026-08-21T23:53:03.134Z",
                                                      "updatedAt": "2026-08-21T23:53:03.134Z"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Nome do comando inválido",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comando não encontrado",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "Comando Inexistente",
                                            value = """
                                                    {
                                                      "statusCode": 404,
                                                      "timestamp": "2026-08-21T23:53:03.499Z",
                                                      "path": "/v2/command/unknown",
                                                      "method": "GET",
                                                      "message": "Command 'unknown' not found",
                                                      "error": "Not Found"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "429",
                            description = "Limite de taxa excedido (Rate Limit)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
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
            description = "Atualiza campos de um comando cadastrado pelo seu identificador (MongoDB ObjectId ou UUID).",
            security = @SecurityRequirement(name = "api-key"),
            requestBody = @RequestBody(
                    description = "Campos do comando a serem atualizados",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateCommandRequest.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de Atualização de Comando",
                                    value = """
                                            {
                                              "description": "Lista atualizada de tecnologias do Gabriel Feijó"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comando atualizado com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CommandResponse.class),
                                    examples = @ExampleObject(
                                            name = "Comando Atualizado",
                                            value = """
                                                    {
                                                      "id": "6a88e4dfd7a2b0617a6db3fa",
                                                      "command": "skills",
                                                      "aliases": ["habilidades", "stack", "techs"],
                                                      "category": "portfolio",
                                                      "description": "Lista atualizada de tecnologias do Gabriel Feijó",
                                                      "language": "pt",
                                                      "response": [
                                                        "☕ Java 21 & Spring Boot 3.3 (Clean Architecture, Virtual Threads)"
                                                      ],
                                                      "createdAt": "2026-08-21T23:53:03.134Z",
                                                      "updatedAt": "2026-08-21T23:55:00.000Z"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Dados ou ID inválidos",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Não autorizado - API Key inválida ou ausente",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comando não encontrado",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<CommandResponse> updateCommand(
            @Parameter(description = "Identificador único do comando (MongoDB ObjectId)", example = "6a88e4dfd7a2b0617a6db3fa")
            @PathVariable("id") String id,
            @Valid @org.springframework.web.bind.annotation.RequestBody UpdateCommandRequest request) {
        CommandResponse response = commandService.updateCommand(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Deletar comando (Requer API Key de Administrador)",
            description = "Exclui permanentemente um comando do catálogo pelo seu identificador único.",
            security = @SecurityRequirement(name = "api-key"),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Comando excluído com sucesso (sem conteúdo no corpo)"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Não autorizado - API Key inválida ou ausente",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comando não encontrado",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<Void> deleteCommand(
            @Parameter(description = "Identificador único do comando (MongoDB ObjectId)", example = "6a88e4dfd7a2b0617a6db3fa")
            @PathVariable("id") String id) {
        commandService.deleteCommand(id);
        return ResponseEntity.noContent().build();
    }
}
