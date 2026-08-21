package br.com.gabrielfeijo.portfolio.infrastructure.persistence.adapter;

import br.com.gabrielfeijo.portfolio.domain.model.Review;
import br.com.gabrielfeijo.portfolio.domain.repository.ReviewRepositoryPort;
import br.com.gabrielfeijo.portfolio.infrastructure.persistence.document.ReviewDocument;
import br.com.gabrielfeijo.portfolio.infrastructure.persistence.mapper.ReviewPersistenceMapper;
import br.com.gabrielfeijo.portfolio.infrastructure.persistence.repository.MongoReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReviewRepositoryAdapter implements ReviewRepositoryPort {

    private final MongoReviewRepository mongoReviewRepository;
    private final ReviewPersistenceMapper reviewPersistenceMapper;

    @Override
    public Review save(Review review) {
        ReviewDocument document = reviewPersistenceMapper.toDocument(review);
        ReviewDocument saved = mongoReviewRepository.save(document);
        return reviewPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Review> findById(String id) {
        return mongoReviewRepository.findById(id)
                .map(reviewPersistenceMapper::toDomain);
    }

    @Override
    public List<Review> findAll(String search, Boolean paginate, Integer page, Integer limit) {
        if (search != null && !search.isBlank()) {
            if (Boolean.TRUE.equals(paginate)) {
                int pageNumber = (page != null && page > 0) ? page - 1 : 0;
                int pageSize = (limit != null && limit > 0) ? limit : 50;
                Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
                return mongoReviewRepository.searchPaged(search.trim(), pageable)
                        .map(reviewPersistenceMapper::toDomain)
                        .getContent();
            }
            return reviewPersistenceMapper.toDomainList(mongoReviewRepository.searchAll(search.trim()));
        }

        if (Boolean.TRUE.equals(paginate)) {
            int pageNumber = (page != null && page > 0) ? page - 1 : 0;
            int pageSize = (limit != null && limit > 0) ? limit : 50;
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
            return mongoReviewRepository.findAll(pageable)
                    .map(reviewPersistenceMapper::toDomain)
                    .getContent();
        }

        return reviewPersistenceMapper.toDomainList(
                mongoReviewRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
        );
    }

    @Override
    public void deleteById(String id) {
        mongoReviewRepository.deleteById(id);
    }
}
