package br.com.gabrielfeijo.portfolio.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Command {
    private String id;
    private String command;
    @Builder.Default
    private List<String> aliases = new ArrayList<>();
    private String category;
    private String description;
    private String language;
    @Builder.Default
    private List<String> response = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;
}
