package br.com.gabrielfeijo.portfolio.application.service;

import br.com.gabrielfeijo.portfolio.application.dto.request.CreateReviewRequest;
import br.com.gabrielfeijo.portfolio.application.dto.request.PaginationQueryRequest;
import br.com.gabrielfeijo.portfolio.application.dto.request.UpdateReviewRequest;
import br.com.gabrielfeijo.portfolio.application.dto.response.ReviewResponse;
import br.com.gabrielfeijo.portfolio.application.mapper.ReviewMapper;
import br.com.gabrielfeijo.portfolio.domain.exception.ResourceNotFoundException;
import br.com.gabrielfeijo.portfolio.domain.model.Review;
import br.com.gabrielfeijo.portfolio.domain.repository.ReviewRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepositoryPort reviewRepositoryPort;
    private final ReviewMapper reviewMapper;

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviews(PaginationQueryRequest query) {
        List<Review> reviews = reviewRepositoryPort.findAll(
                query.search(),
                query.paginate(),
                query.page(),
                query.limit()
        );
        return reviewMapper.toResponseList(reviews);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(String id) {
        Review review = reviewRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review with ID " + id + " not found"));
        return reviewMapper.toResponse(review);
    }

    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request) {
        Review review = reviewMapper.toDomain(request);
        review.setUsername(request.username().trim());
        review.setComment(request.comment().trim());
        Review saved = reviewRepositoryPort.save(review);
        return reviewMapper.toResponse(saved);
    }

    @Transactional
    public ReviewResponse updateReview(String id, UpdateReviewRequest request) {
        Review existing = reviewRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review with ID " + id + " not found"));

        if (request.username() != null && !request.username().isBlank()) {
            existing.setUsername(request.username().trim());
        }
        if (request.comment() != null && !request.comment().isBlank()) {
            existing.setComment(request.comment().trim());
        }
        if (request.stars() != null) {
            existing.setStars(request.stars());
        }

        Review updated = reviewRepositoryPort.save(existing);
        return reviewMapper.toResponse(updated);
    }

    @Transactional
    public void deleteReview(String id) {
        if (reviewRepositoryPort.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Review with ID " + id + " not found");
        }
        reviewRepositoryPort.deleteById(id);
    }
}
