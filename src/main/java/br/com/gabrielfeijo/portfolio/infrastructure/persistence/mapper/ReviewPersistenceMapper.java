package br.com.gabrielfeijo.portfolio.infrastructure.persistence.mapper;

import br.com.gabrielfeijo.portfolio.domain.model.Review;
import br.com.gabrielfeijo.portfolio.infrastructure.persistence.document.ReviewDocument;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewPersistenceMapper {
    Review toDomain(ReviewDocument document);
    ReviewDocument toDocument(Review domain);
    List<Review> toDomainList(List<ReviewDocument> documents);
}
