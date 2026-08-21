package br.com.gabrielfeijo.portfolio.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateReviewRequest(
        @Schema(example = "John Doe", description = "O nome que será exibido na review", minLength = 2, maxLength = 100)
        @NotBlank(message = "Username is required")
        @Size(min = 2, max = 100, message = "Username must be between 2 and 100 characters")
        @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_.]+$", message = "Username contains invalid characters")
        String username,

        @Schema(example = "Excelente portfólio e projetos muito bem estruturados!", description = "O comentário que será exibido na review", minLength = 10, maxLength = 500)
        @NotBlank(message = "Comment is required")
        @Size(min = 10, max = 500, message = "Comment must be between 10 and 500 characters")
        String comment,

        @Schema(example = "5", description = "A quantidade de estrelas (0-5)", minimum = "0", maximum = "5")
        @NotNull(message = "Stars is required")
        @Min(value = 0, message = "Stars must be at least 0")
        @Max(value = 5, message = "Stars must not exceed 5")
        Integer stars
) {}
