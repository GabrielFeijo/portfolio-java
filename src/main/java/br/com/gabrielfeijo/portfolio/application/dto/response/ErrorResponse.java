package br.com.gabrielfeijo.portfolio.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        @Schema(example = "400", description = "Código de status HTTP")
        Integer statusCode,

        @Schema(example = "2026-08-21T20:25:00.000Z", description = "Data e hora do erro")
        Instant timestamp,

        @Schema(example = "/v2/command", description = "URI requisitada")
        String path,

        @Schema(example = "POST", description = "Método HTTP")
        String method,

        @Schema(example = "Command is required", description = "Mensagem descritiva do erro")
        Object message,

        @Schema(example = "Bad Request", description = "Nome ou categoria do erro")
        String error
) {}
