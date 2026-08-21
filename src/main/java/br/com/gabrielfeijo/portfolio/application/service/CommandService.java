package br.com.gabrielfeijo.portfolio.application.service;

import br.com.gabrielfeijo.portfolio.application.dto.request.CreateCommandRequest;
import br.com.gabrielfeijo.portfolio.application.dto.request.PaginationQueryRequest;
import br.com.gabrielfeijo.portfolio.application.dto.request.UpdateCommandRequest;
import br.com.gabrielfeijo.portfolio.application.dto.response.CommandResponse;
import br.com.gabrielfeijo.portfolio.application.mapper.CommandMapper;
import br.com.gabrielfeijo.portfolio.domain.exception.BusinessValidationException;
import br.com.gabrielfeijo.portfolio.domain.exception.DuplicateResourceException;
import br.com.gabrielfeijo.portfolio.domain.exception.ResourceNotFoundException;
import br.com.gabrielfeijo.portfolio.domain.model.Command;
import br.com.gabrielfeijo.portfolio.domain.repository.CommandRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandService {

    private final CommandRepositoryPort commandRepositoryPort;
    private final CommandMapper commandMapper;

    @Transactional
    public CommandResponse createCommand(CreateCommandRequest request) {
        String normalizedCommand = sanitizeCommand(request.command());
        if (commandRepositoryPort.existsByCommand(normalizedCommand)) {
            throw new DuplicateResourceException("Command '" + normalizedCommand + "' already exists");
        }

        Command command = commandMapper.toDomain(request);
        command.setCommand(normalizedCommand);

        if (command.getAliases() == null) {
            command.setAliases(new ArrayList<>());
        } else {
            command.setAliases(command.getAliases().stream()
                    .map(this::sanitizeCommand)
                    .filter(s -> !s.isBlank())
                    .distinct()
                    .toList());
        }

        if (command.getCategory() == null || command.getCategory().isBlank()) {
            command.setCategory("general");
        }
        if (command.getDescription() == null) {
            command.setDescription("");
        }
        if (command.getLanguage() == null || command.getLanguage().isBlank()) {
            command.setLanguage("all");
        }

        Command savedCommand = commandRepositoryPort.save(command);
        return commandMapper.toResponse(savedCommand);
    }

    @Transactional(readOnly = true)
    public List<CommandResponse> getCommands(PaginationQueryRequest query) {
        List<Command> commands = commandRepositoryPort.findAll(
                query.search(),
                query.paginate(),
                query.page(),
                query.limit()
        );
        return commandMapper.toResponseList(commands);
    }

    @Transactional(readOnly = true)
    public CommandResponse getCommandByName(String rawCommand) {
        if (rawCommand == null || rawCommand.length() < 2 || rawCommand.length() > 100) {
            throw new BusinessValidationException("Command name must be between 2 and 100 characters");
        }

        String sanitized = sanitizeCommand(rawCommand);
        Command command = commandRepositoryPort.findByCommandOrAlias(sanitized)
                .orElseThrow(() -> new ResourceNotFoundException("Command '" + rawCommand + "' not found"));

        return commandMapper.toResponse(command);
    }

    @Transactional
    public CommandResponse updateCommand(String id, UpdateCommandRequest request) {
        Command existing = commandRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Command with ID " + id + " not found"));

        if (request.command() != null && !request.command().isBlank()) {
            String sanitized = sanitizeCommand(request.command());
            if (commandRepositoryPort.existsByCommandAndIdNot(sanitized, id)) {
                throw new DuplicateResourceException("Command '" + sanitized + "' already exists");
            }
            existing.setCommand(sanitized);
        }

        if (request.aliases() != null) {
            existing.setAliases(request.aliases().stream()
                    .map(this::sanitizeCommand)
                    .filter(s -> !s.isBlank())
                    .distinct()
                    .toList());
        }

        if (request.category() != null) {
            existing.setCategory(request.category());
        }
        if (request.description() != null) {
            existing.setDescription(request.description());
        }
        if (request.language() != null) {
            existing.setLanguage(request.language());
        }
        if (request.response() != null && !request.response().isEmpty()) {
            existing.setResponse(request.response());
        }

        Command updated = commandRepositoryPort.save(existing);
        return commandMapper.toResponse(updated);
    }

    @Transactional
    public void deleteCommand(String id) {
        if (commandRepositoryPort.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Command with ID " + id + " not found");
        }
        commandRepositoryPort.deleteById(id);
    }

    public String sanitizeCommand(String input) {
        if (input == null) return "";
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9._\\- ]", "")
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase();
    }
}
