package br.com.gabrielfeijo.portfolio.infrastructure.web.controller;

import br.com.gabrielfeijo.portfolio.application.dto.request.CreateReviewRequest;
import br.com.gabrielfeijo.portfolio.application.dto.request.PaginationQueryRequest;
import br.com.gabrielfeijo.portfolio.application.dto.request.UpdateReviewRequest;
import br.com.gabrielfeijo.portfolio.application.dto.response.ReviewResponse;
import br.com.gabrielfeijo.portfolio.application.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v2/review")
@Tag(name = "Review", description = "Endpoints para gerenciamento de reviews e depoimentos de visitantes")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    @Operation(
            summary = "Listar todas as reviews (com suporte a busca e paginação)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de reviews retornada"),
                    @ApiResponse(responseCode = "429", description = "Too Many Requests")
            }
    )
    public ResponseEntity<List<ReviewResponse>> getReviews(@ParameterObject @ModelAttribute PaginationQueryRequest query) {
        List<ReviewResponse> reviews = reviewService.getReviews(query);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar review por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Review encontrada"),
                    @ApiResponse(responseCode = "400", description = "ID inválido"),
                    @ApiResponse(responseCode = "404", description = "Review não encontrada"),
                    @ApiResponse(responseCode = "429", description = "Too Many Requests")
            }
    )
    public ResponseEntity<ReviewResponse> getReviewById(
            @Parameter(description = "Identificador único da review", example = "65d4f1a2e4b0a123456789ac")
            @PathVariable("id") String id) {
        ReviewResponse response = reviewService.getReviewById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar nova review pública",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Review criada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "429", description = "Too Many Requests")
            }
    )
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody CreateReviewRequest request) {
        ReviewResponse response = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar review existente (Requer API Key de Administrador)",
            security = @SecurityRequirement(name = "api-key"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Review atualizada"),
                    @ApiResponse(responseCode = "400", description = "ID ou dados inválidos"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado - API Key inválida ou ausente"),
                    @ApiResponse(responseCode = "404", description = "Review não encontrada")
            }
    )
    public ResponseEntity<ReviewResponse> updateReview(
            @Parameter(description = "Identificador único da review", example = "65d4f1a2e4b0a123456789ac")
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateReviewRequest request) {
        ReviewResponse response = reviewService.updateReview(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Deletar review (Requer API Key de Administrador)",
            security = @SecurityRequirement(name = "api-key"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Review deletada"),
                    @ApiResponse(responseCode = "400", description = "ID inválido"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado - API Key inválida ou ausente"),
                    @ApiResponse(responseCode = "404", description = "Review não encontrada")
            }
    )
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "Identificador único da review", example = "65d4f1a2e4b0a123456789ac")
            @PathVariable("id") String id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
