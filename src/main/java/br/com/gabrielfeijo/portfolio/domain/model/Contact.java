package br.com.gabrielfeijo.portfolio.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contact {
    private String id;
    private String name;
    private String email;
    private String message;
    private Instant createdAt;
    private Instant updatedAt;
}
