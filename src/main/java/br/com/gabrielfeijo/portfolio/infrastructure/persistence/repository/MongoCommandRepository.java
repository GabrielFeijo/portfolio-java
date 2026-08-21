package br.com.gabrielfeijo.portfolio.infrastructure.persistence.repository;

import br.com.gabrielfeijo.portfolio.infrastructure.persistence.document.CommandDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MongoCommandRepository extends MongoRepository<CommandDocument, String> {

    Optional<CommandDocument> findByCommand(String command);

    @Query("{ '$or': [ { 'command': ?0 }, { 'aliases': ?0 } ] }")
    Optional<CommandDocument> findByCommandOrAlias(String term);

    boolean existsByCommand(String command);

    boolean existsByCommandAndIdNot(String command, String id);

    @Query("{ '$or': [ " +
           "{ 'command': { '$regex': ?0, '$options': 'i' } }, " +
           "{ 'aliases': { '$regex': ?0, '$options': 'i' } }, " +
           "{ 'description': { '$regex': ?0, '$options': 'i' } }, " +
           "{ 'category': { '$regex': ?0, '$options': 'i' } } ] }")
    List<CommandDocument> searchAll(String search);

    @Query("{ '$or': [ " +
           "{ 'command': { '$regex': ?0, '$options': 'i' } }, " +
           "{ 'aliases': { '$regex': ?0, '$options': 'i' } }, " +
           "{ 'description': { '$regex': ?0, '$options': 'i' } }, " +
           "{ 'category': { '$regex': ?0, '$options': 'i' } } ] }")
    Page<CommandDocument> searchPaged(String search, Pageable pageable);
}
