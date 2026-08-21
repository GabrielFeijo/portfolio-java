package br.com.gabrielfeijo.portfolio.infrastructure.persistence.repository;

import br.com.gabrielfeijo.portfolio.infrastructure.persistence.document.ReviewDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoReviewRepository extends MongoRepository<ReviewDocument, String> {

    @Query(value = "{ '$or': [ { 'username': { '$regex': ?0, '$options': 'i' } }, { 'comment': { '$regex': ?0, '$options': 'i' } } ] }", sort = "{ 'createdAt': -1 }")
    List<ReviewDocument> searchAll(String search);

    @Query(value = "{ '$or': [ { 'username': { '$regex': ?0, '$options': 'i' } }, { 'comment': { '$regex': ?0, '$options': 'i' } } ] }")
    Page<ReviewDocument> searchPaged(String search, Pageable pageable);
}
