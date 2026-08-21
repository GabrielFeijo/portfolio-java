package br.com.gabrielfeijo.portfolio.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ContactResponse(
        @Schema(example = "65d4f1a2e4b0a123456789ad", description = "Identificador único do contato")
        String id,

        @Schema(example = "Gabriel Feijó", description = "Nome")
        String name,

        @Schema(example = "gabriel@email.com", description = "Email de contato")
        String email,

        @Schema(example = "Gostaria de conversar sobre uma oportunidade de projeto.", description = "Mensagem enviada")
        String message,

        @Schema(description = "Data de envio")
        Instant createdAt,

        @Schema(description = "Data de atualização")
        Instant updatedAt
) {}
