package br.com.gabrielfeijo.portfolio.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommandResponse(
        @Schema(example = "65d4f1a2e4b0a123456789ab", description = "Identificador único do comando")
        String id,

        @Schema(example = "skills", description = "Comando disparador")
        String command,

        @Schema(example = "[\"habilidades\", \"stack\"]", description = "Aliases do comando")
        List<String> aliases,

        @Schema(example = "portfolio", description = "Categoria")
        String category,

        @Schema(example = "Lista as principais tecnologias e habilidades", description = "Descrição")
        String description,

        @Schema(example = "all", description = "Idioma")
        String language,

        @Schema(example = "[\"React, TypeScript, Spring Boot, PostgreSQL\"]", description = "Respostas do comando")
        List<String> response,

        @Schema(description = "Data de criação")
        Instant createdAt,

        @Schema(description = "Data de atualização")
        Instant updatedAt
) {}
