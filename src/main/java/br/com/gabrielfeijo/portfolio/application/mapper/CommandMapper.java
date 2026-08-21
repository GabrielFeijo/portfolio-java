package br.com.gabrielfeijo.portfolio.application.mapper;

import br.com.gabrielfeijo.portfolio.application.dto.request.CreateCommandRequest;
import br.com.gabrielfeijo.portfolio.application.dto.request.UpdateCommandRequest;
import br.com.gabrielfeijo.portfolio.application.dto.response.CommandResponse;
import br.com.gabrielfeijo.portfolio.domain.model.Command;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommandMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Command toDomain(CreateCommandRequest request);

    CommandResponse toResponse(Command command);

    List<CommandResponse> toResponseList(List<Command> commands);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateDomainFromRequest(UpdateCommandRequest request, @MappingTarget Command command);
}
