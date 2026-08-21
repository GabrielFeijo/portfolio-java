package br.com.gabrielfeijo.portfolio.infrastructure.persistence.mapper;

import br.com.gabrielfeijo.portfolio.domain.model.Command;
import br.com.gabrielfeijo.portfolio.infrastructure.persistence.document.CommandDocument;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommandPersistenceMapper {
    Command toDomain(CommandDocument document);
    CommandDocument toDocument(Command domain);
    List<Command> toDomainList(List<CommandDocument> documents);
}
