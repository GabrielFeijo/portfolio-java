package br.com.gabrielfeijo.portfolio.application.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record PaginationQueryRequest(
        @Parameter(description = "Se verdadeiro, ativa a paginação dos resultados. Padrão: false (retorna todos os registros)", schema = @Schema(defaultValue = "false"))
        Boolean paginate,

        @Parameter(description = "Número da página (quando paginate=true)", schema = @Schema(minimum = "1"))
        @Min(value = 1, message = "Page must be at least 1")
        Integer page,

        @Parameter(description = "Quantidade de registros por página (quando paginate=true)", schema = @Schema(minimum = "1", maximum = "100"))
        @Min(value = 1, message = "Limit must be at least 1")
        @Max(value = 100, message = "Limit cannot exceed 100")
        Integer limit,

        @Parameter(description = "Termo de busca opcional para filtrar os resultados")
        String search
) {
    public PaginationQueryRequest {
        if (paginate == null) {
            paginate = false;
        }
        if (page == null) {
            page = 1;
        }
        if (limit == null) {
            limit = 50;
        }
    }
}
