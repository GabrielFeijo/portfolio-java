package br.com.gabrielfeijo.portfolio.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReviewResponse(
        @Schema(example = "65d4f1a2e4b0a123456789ac", description = "Identificador único da review")
        String id,

        @Schema(example = "John Doe", description = "Nome do autor da review")
        String username,

        @Schema(example = "Excelente portfólio e projetos muito bem estruturados!", description = "Comentário da review")
        String comment,

        @Schema(example = "5", description = "Nota de avaliação em estrelas")
        Integer stars,

        @Schema(description = "Data de criação")
        Instant createdAt,

        @Schema(description = "Data de atualização")
        Instant updatedAt
) {}
