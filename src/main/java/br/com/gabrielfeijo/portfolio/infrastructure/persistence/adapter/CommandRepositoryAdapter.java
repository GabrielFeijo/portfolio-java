package br.com.gabrielfeijo.portfolio.infrastructure.persistence.adapter;

import br.com.gabrielfeijo.portfolio.domain.model.Command;
import br.com.gabrielfeijo.portfolio.domain.repository.CommandRepositoryPort;
import br.com.gabrielfeijo.portfolio.infrastructure.persistence.document.CommandDocument;
import br.com.gabrielfeijo.portfolio.infrastructure.persistence.mapper.CommandPersistenceMapper;
import br.com.gabrielfeijo.portfolio.infrastructure.persistence.repository.MongoCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommandRepositoryAdapter implements CommandRepositoryPort {

    private final MongoCommandRepository mongoCommandRepository;
    private final CommandPersistenceMapper commandPersistenceMapper;

    @Override
    public Command save(Command command) {
        CommandDocument document = commandPersistenceMapper.toDocument(command);
        CommandDocument saved = mongoCommandRepository.save(document);
        return commandPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Command> findById(String id) {
        return mongoCommandRepository.findById(id)
                .map(commandPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Command> findByCommand(String command) {
        return mongoCommandRepository.findByCommand(command)
                .map(commandPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Command> findByCommandOrAlias(String commandOrAlias) {
        return mongoCommandRepository.findByCommandOrAlias(commandOrAlias)
                .map(commandPersistenceMapper::toDomain);
    }

    @Override
    public List<Command> findAll(String search, Boolean paginate, Integer page, Integer limit) {
        if (search != null && !search.isBlank()) {
            if (Boolean.TRUE.equals(paginate)) {
                int pageNumber = (page != null && page > 0) ? page - 1 : 0;
                int pageSize = (limit != null && limit > 0) ? limit : 50;
                Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "command"));
                return mongoCommandRepository.searchPaged(search.trim(), pageable)
                        .map(commandPersistenceMapper::toDomain)
                        .getContent();
            }
            return commandPersistenceMapper.toDomainList(mongoCommandRepository.searchAll(search.trim()));
        }

        if (Boolean.TRUE.equals(paginate)) {
            int pageNumber = (page != null && page > 0) ? page - 1 : 0;
            int pageSize = (limit != null && limit > 0) ? limit : 50;
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "command"));
            return mongoCommandRepository.findAll(pageable)
                    .map(commandPersistenceMapper::toDomain)
                    .getContent();
        }

        return commandPersistenceMapper.toDomainList(
                mongoCommandRepository.findAll(Sort.by(Sort.Direction.ASC, "command"))
        );
    }

    @Override
    public boolean existsByCommand(String command) {
        return mongoCommandRepository.existsByCommand(command);
    }

    @Override
    public boolean existsByCommandAndIdNot(String command, String id) {
        return mongoCommandRepository.existsByCommandAndIdNot(command, id);
    }

    @Override
    public void deleteById(String id) {
        mongoCommandRepository.deleteById(id);
    }
}
