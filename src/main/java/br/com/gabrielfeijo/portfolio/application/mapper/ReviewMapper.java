package br.com.gabrielfeijo.portfolio.application.mapper;

import br.com.gabrielfeijo.portfolio.application.dto.request.CreateReviewRequest;
import br.com.gabrielfeijo.portfolio.application.dto.request.UpdateReviewRequest;
import br.com.gabrielfeijo.portfolio.application.dto.response.ReviewResponse;
import br.com.gabrielfeijo.portfolio.domain.model.Review;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Review toDomain(CreateReviewRequest request);

    ReviewResponse toResponse(Review review);

    List<ReviewResponse> toResponseList(List<Review> reviews);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateDomainFromRequest(UpdateReviewRequest request, @MappingTarget Review review);
}
