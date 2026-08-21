package br.com.gabrielfeijo.portfolio.infrastructure.persistence.repository;

import br.com.gabrielfeijo.portfolio.infrastructure.persistence.document.ContactDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoContactRepository extends MongoRepository<ContactDocument, String> {
}
