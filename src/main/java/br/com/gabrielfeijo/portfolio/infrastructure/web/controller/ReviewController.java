package br.com.gabrielfeijo.portfolio.infrastructure.web.controller;

import br.com.gabrielfeijo.portfolio.application.dto.request.CreateReviewRequest;
import br.com.gabrielfeijo.portfolio.application.dto.request.PaginationQueryRequest;
import br.com.gabrielfeijo.portfolio.application.dto.request.UpdateReviewRequest;
import br.com.gabrielfeijo.portfolio.application.dto.response.ErrorResponse;
import br.com.gabrielfeijo.portfolio.application.dto.response.ReviewResponse;
import br.com.gabrielfeijo.portfolio.application.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v2/review")
@Tag(name = "Review", description = "Endpoints para envio, listagem e moderação de reviews e depoimentos")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    @Operation(
            summary = "Listar todas as reviews (com busca e paginação)",
            description = "Retorna a listagem de avaliações em ordem cronológica decrescente com suporte a busca textual por autor ou comentário.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de reviews retornada com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = ReviewResponse.class)),
                                    examples = @ExampleObject(
                                            name = "Lista de Depoimentos",
                                            value = """
                                                    [
                                                      {
                                                        "id": "6a88e4dfd7a2b0617a6db3fb",
                                                        "username": "Tech Lead Recruiter",
                                                        "comment": "Excelente domínio de Java 21, Spring Boot 3 e Clean Architecture!",
                                                        "stars": 5,
                                                        "createdAt": "2026-08-21T23:53:03.328Z",
                                                        "updatedAt": "2026-08-21T23:53:03.328Z"
                                                      }
                                                    ]
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "429",
                            description = "Limite de taxa excedido (Rate Limit)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<List<ReviewResponse>> getReviews(@ParameterObject @ModelAttribute PaginationQueryRequest query) {
        List<ReviewResponse> reviews = reviewService.getReviews(query);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar review por ID",
            description = "Recupera uma avaliação cadastrada a partir do seu identificador único.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Review localizada",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ReviewResponse.class),
                                    examples = @ExampleObject(
                                            name = "Review Detalhada",
                                            value = """
                                                    {
                                                      "id": "6a88e4dfd7a2b0617a6db3fb",
                                                      "username": "Tech Lead Recruiter",
                                                      "comment": "Excelente domínio de Java 21, Spring Boot 3 e Clean Architecture!",
                                                      "stars": 5,
                                                      "createdAt": "2026-08-21T23:53:03.328Z",
                                                      "updatedAt": "2026-08-21T23:53:03.328Z"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Review não encontrada",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "429",
                            description = "Limite de taxa excedido",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<ReviewResponse> getReviewById(
            @Parameter(description = "Identificador único da review (MongoDB ObjectId)", example = "6a88e4dfd7a2b0617a6db3fb")
            @PathVariable("id") String id) {
        ReviewResponse response = reviewService.getReviewById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar nova review pública",
            description = "Permite que qualquer visitante envie uma avaliação pública com autor, comentário e quantidade de estrelas (0 a 5).",
            requestBody = @RequestBody(
                    description = "Dados da avaliação enviada pelo visitante",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateReviewRequest.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de Envio de Review",
                                    value = """
                                            {
                                              "username": "Tech Lead Recruiter",
                                              "comment": "Excelente domínio de Java 21, Spring Boot 3 e Clean Architecture!",
                                              "stars": 5
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Review registrada com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ReviewResponse.class),
                                    examples = @ExampleObject(
                                            name = "Review Criada",
                                            value = """
                                                    {
                                                      "id": "6a88e4dfd7a2b0617a6db3fb",
                                                      "username": "Tech Lead Recruiter",
                                                      "comment": "Excelente domínio de Java 21, Spring Boot 3 e Clean Architecture!",
                                                      "stars": 5,
                                                      "createdAt": "2026-08-21T23:53:03.328Z",
                                                      "updatedAt": "2026-08-21T23:53:03.328Z"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Dados inválidos (estrelas fora do intervalo 0-5 ou texto curto)",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "Erro de Validação",
                                            value = """
                                                    {
                                                      "statusCode": 400,
                                                      "timestamp": "2026-08-21T23:53:03.499Z",
                                                      "path": "/v2/review",
                                                      "method": "POST",
                                                      "message": "Stars must not exceed 5",
                                                      "error": "Bad Request"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "429",
                            description = "Limite de taxa excedido",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<ReviewResponse> createReview(@Valid @org.springframework.web.bind.annotation.RequestBody CreateReviewRequest request) {
        ReviewResponse response = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar review existente (Requer API Key de Administrador)",
            description = "Permite a moderação administrativa do conteúdo ou nota de uma review cadastrada.",
            security = @SecurityRequirement(name = "api-key"),
            requestBody = @RequestBody(
                    description = "Campos a serem atualizados",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateReviewRequest.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de Moderação de Review",
                                    value = """
                                            {
                                              "comment": "Comentário revisado e aprovado pela moderação.",
                                              "stars": 5
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Review atualizada com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ReviewResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Não autorizado - API Key inválida ou ausente",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Review não encontrada",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<ReviewResponse> updateReview(
            @Parameter(description = "Identificador único da review (MongoDB ObjectId)", example = "6a88e4dfd7a2b0617a6db3fb")
            @PathVariable("id") String id,
            @Valid @org.springframework.web.bind.annotation.RequestBody UpdateReviewRequest request) {
        ReviewResponse response = reviewService.updateReview(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Deletar review (Requer API Key de Administrador)",
            description = "Remove permanentemente uma avaliação.",
            security = @SecurityRequirement(name = "api-key"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Review excluída com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Review não encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "Identificador único da review (MongoDB ObjectId)", example = "6a88e4dfd7a2b0617a6db3fb")
            @PathVariable("id") String id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
