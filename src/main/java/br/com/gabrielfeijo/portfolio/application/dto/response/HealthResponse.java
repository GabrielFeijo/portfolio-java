package br.com.gabrielfeijo.portfolio.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record HealthResponse(
        @Schema(example = "UP", description = "Status geral de integridade da aplicação")
        String status,

        @Schema(description = "Timestamp UTC da verificação")
        Instant timestamp,

        @Schema(example = "124s", description = "Tempo de atividade contínua da aplicação")
        String uptime,

        @Schema(description = "Metadados do serviço e ambiente")
        Map<String, Object> application,

        @Schema(description = "Integridade e latência dos componentes de persistência")
        Map<String, Object> components,

        @Schema(description = "Estatísticas de memória e recursos do sistema")
        Map<String, Object> system
) {}
