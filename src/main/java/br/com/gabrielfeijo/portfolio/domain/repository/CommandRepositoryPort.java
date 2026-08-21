package br.com.gabrielfeijo.portfolio.domain.repository;

import br.com.gabrielfeijo.portfolio.domain.model.Command;

import java.util.List;
import java.util.Optional;

public interface CommandRepositoryPort {
    Command save(Command command);
    Optional<Command> findById(String id);
    Optional<Command> findByCommand(String command);
    Optional<Command> findByCommandOrAlias(String commandOrAlias);
    List<Command> findAll(String search, Boolean paginate, Integer page, Integer limit);
    boolean existsByCommand(String command);
    boolean existsByCommandAndIdNot(String command, String id);
    void deleteById(String id);
}
