package br.com.gabrielfeijo.portfolio.infrastructure.web.controller;

import br.com.gabrielfeijo.portfolio.application.dto.request.CreateContactRequest;
import br.com.gabrielfeijo.portfolio.application.dto.response.ContactResponse;
import br.com.gabrielfeijo.portfolio.application.dto.response.ErrorResponse;
import br.com.gabrielfeijo.portfolio.application.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/contact")
@Tag(name = "Contact", description = "Endpoints para recebimento e processamento de mensagens de contato")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Registrar mensagem enviada pelo formulário de contato",
            description = "Recebe e valida dados de contato (nome, e-mail válido e mensagem) originados pelo formulário do portfólio.",
            requestBody = @RequestBody(
                    description = "Dados da mensagem de contato",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateContactRequest.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de Envio de Contato",
                                    value = """
                                            {
                                              "name": "Recrutador Tech",
                                              "email": "recrutador@empresa.com",
                                              "message": "Olá Gabriel, vimos sua API Java com Spring Boot e gostaríamos de agendar uma conversa técnica."
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Mensagem de contato registrada com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ContactResponse.class),
                                    examples = @ExampleObject(
                                            name = "Contato Registrado",
                                            value = """
                                                    {
                                                      "id": "6a88e4dfd7a2b0617a6db3fc",
                                                      "name": "Recrutador Tech",
                                                      "email": "recrutador@empresa.com",
                                                      "message": "Olá Gabriel, vimos sua API Java com Spring Boot e gostaríamos de agendar uma conversa técnica.",
                                                      "createdAt": "2026-08-21T23:53:03.449Z",
                                                      "updatedAt": "2026-08-21T23:53:03.449Z"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Dados inválidos (e-mail malformado ou campos vazios)",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "Erro de Validação de E-mail",
                                            value = """
                                                    {
                                                      "statusCode": 400,
                                                      "timestamp": "2026-08-21T23:53:03.499Z",
                                                      "path": "/v2/contact",
                                                      "method": "POST",
                                                      "message": "Email must be valid",
                                                      "error": "Bad Request"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "429",
                            description = "Limite de taxa excedido (Rate Limit: 2 req / 10s)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<ContactResponse> createContact(@Valid @org.springframework.web.bind.annotation.RequestBody CreateContactRequest request) {
        ContactResponse response = contactService.createContact(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
