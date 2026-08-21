package br.com.gabrielfeijo.portfolio.application.service;

import br.com.gabrielfeijo.portfolio.application.dto.request.CreateReviewRequest;
import br.com.gabrielfeijo.portfolio.application.dto.request.PaginationQueryRequest;
import br.com.gabrielfeijo.portfolio.application.dto.request.UpdateReviewRequest;
import br.com.gabrielfeijo.portfolio.application.dto.response.ReviewResponse;
import br.com.gabrielfeijo.portfolio.application.mapper.ReviewMapper;
import br.com.gabrielfeijo.portfolio.domain.exception.ResourceNotFoundException;
import br.com.gabrielfeijo.portfolio.domain.model.Review;
import br.com.gabrielfeijo.portfolio.domain.repository.ReviewRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepositoryPort reviewRepositoryPort;

    @Spy
    private ReviewMapper reviewMapper = Mappers.getMapper(ReviewMapper.class);

    @InjectMocks
    private ReviewService reviewService;

    private Review sampleReview;

    @BeforeEach
    void setUp() {
        sampleReview = Review.builder()
                .id("rev-123")
                .username("John Doe")
                .comment("Excelente trabalho e organização!")
                .stars(5)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar review com sucesso")
    void shouldCreateReviewSuccessfully() {
        CreateReviewRequest request = new CreateReviewRequest(
                "  John Doe  ",
                "  Excelente trabalho e organização!  ",
                5
        );

        when(reviewRepositoryPort.save(any(Review.class))).thenReturn(sampleReview);

        ReviewResponse response = reviewService.createReview(request);

        assertThat(response).isNotNull();
        assertThat(response.username()).isEqualTo("John Doe");
        assertThat(response.stars()).isEqualTo(5);
        verify(reviewRepositoryPort).save(any(Review.class));
    }

    @Test
    @DisplayName("Deve buscar review por ID")
    void shouldGetReviewById() {
        when(reviewRepositoryPort.findById("rev-123")).thenReturn(Optional.of(sampleReview));

        ReviewResponse response = reviewService.getReviewById("rev-123");

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("rev-123");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException para review inexistente")
    void shouldThrowNotFoundWhenReviewDoesNotExist() {
        when(reviewRepositoryPort.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.getReviewById("unknown"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve atualizar review com sucesso")
    void shouldUpdateReviewSuccessfully() {
        UpdateReviewRequest request = new UpdateReviewRequest("Jane Doe", "Novo comentário super detalhado", 4);
        when(reviewRepositoryPort.findById("rev-123")).thenReturn(Optional.of(sampleReview));
        when(reviewRepositoryPort.save(any(Review.class))).thenReturn(sampleReview);

        ReviewResponse response = reviewService.updateReview("rev-123", request);

        assertThat(response).isNotNull();
        verify(reviewRepositoryPort).save(any(Review.class));
    }

    @Test
    @DisplayName("Deve deletar review por ID")
    void shouldDeleteReviewSuccessfully() {
        when(reviewRepositoryPort.findById("rev-123")).thenReturn(Optional.of(sampleReview));

        reviewService.deleteReview("rev-123");

        verify(reviewRepositoryPort).deleteById("rev-123");
    }
}
