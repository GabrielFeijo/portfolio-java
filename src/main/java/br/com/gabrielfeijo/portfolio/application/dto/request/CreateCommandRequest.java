package br.com.gabrielfeijo.portfolio.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateCommandRequest(
        @Schema(example = "skills", description = "O comando que será utilizado para disparar a resposta.", minLength = 2, maxLength = 100)
        @NotBlank(message = "Command is required")
        @Size(min = 2, max = 100, message = "Command must be between 2 and 100 characters")
        @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_.]+$", message = "Command contains invalid characters")
        String command,

        @Schema(example = "[\"habilidades\", \"stack\"]", description = "Nomes alternativos para o comando.")
        List<@NotBlank(message = "Alias cannot be blank") @Size(max = 100) String> aliases,

        @Schema(example = "portfolio", description = "Categoria do comando.", defaultValue = "general")
        String category,

        @Schema(example = "Lista as principais tecnologias e habilidades", description = "Descrição curta do comando.", defaultValue = "")
        String description,

        @Schema(example = "all", description = "Idioma do comando (pt, en, all).", defaultValue = "all")
        String language,

        @Schema(example = "[\"React, TypeScript, Node.js, NestJS, Spring Boot, PostgreSQL\"]", description = "A resposta do comando (máximo 20 itens).")
        @NotEmpty(message = "Response is required")
        @Size(min = 1, max = 20, message = "Response must contain between 1 and 20 items")
        List<@NotBlank(message = "Response item cannot be blank") @Size(max = 500, message = "Each response item must not exceed 500 characters") String> response
) {}
