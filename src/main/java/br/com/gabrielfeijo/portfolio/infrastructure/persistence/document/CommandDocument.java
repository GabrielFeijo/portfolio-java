package br.com.gabrielfeijo.portfolio.infrastructure.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "commands")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String command;

    @Builder.Default
    private List<String> aliases = new ArrayList<>();

    @Builder.Default
    private String category = "general";

    @Builder.Default
    private String description = "";

    @Builder.Default
    private String language = "all";

    @Builder.Default
    private List<String> response = new ArrayList<>();

    @CreatedDate
    @Field("createdAt")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updatedAt")
    private Instant updatedAt;
}
