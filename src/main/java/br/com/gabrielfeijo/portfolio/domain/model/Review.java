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
public class Review {
    private String id;
    private String username;
    private String comment;
    private Integer stars;
    private Instant createdAt;
    private Instant updatedAt;
}
