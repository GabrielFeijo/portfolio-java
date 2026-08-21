package br.com.gabrielfeijo.portfolio.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateContactRequest(
        @Schema(example = "Gabriel Feijó", description = "Nome da pessoa que enviou a mensagem", minLength = 2, maxLength = 120)
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 120, message = "Name must be between 2 and 120 characters")
        String name,

        @Schema(example = "gabriel@email.com", description = "Email para retorno", maxLength = 160)
        @NotBlank(message = "Email is required")
        @Size(max = 160, message = "Email must not exceed 160 characters")
        @Email(message = "Email must be valid")
        String email,

        @Schema(example = "Gostaria de conversar sobre uma oportunidade de projeto de software.", description = "Mensagem enviada pelo formulário de contato", minLength = 12, maxLength = 600)
        @NotBlank(message = "Message is required")
        @Size(min = 12, max = 600, message = "Message must be between 12 and 600 characters")
        String message
) {}
