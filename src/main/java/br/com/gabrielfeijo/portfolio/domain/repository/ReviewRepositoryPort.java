package br.com.gabrielfeijo.portfolio.domain.repository;

import br.com.gabrielfeijo.portfolio.domain.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepositoryPort {
    Review save(Review review);
    Optional<Review> findById(String id);
    List<Review> findAll(String search, Boolean paginate, Integer page, Integer limit);
    void deleteById(String id);
}
