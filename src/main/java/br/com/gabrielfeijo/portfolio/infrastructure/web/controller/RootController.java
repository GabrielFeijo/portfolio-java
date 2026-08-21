package br.com.gabrielfeijo.portfolio.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "Root", description = "Endpoints raiz e verificação de integridade")
public class RootController {

    @GetMapping(value = {"/", "/v2", "/v2/"}, produces = "text/plain")
    @Operation(summary = "Health check / Boas-vindas")
    public ResponseEntity<String> getHello() {
        return ResponseEntity.ok("Hello World!");
    }

    @GetMapping(value = "/v2/info", produces = "application/json")
    @Operation(summary = "Informações da API")
    public ResponseEntity<Map<String, Object>> getInfo() {
        return ResponseEntity.ok(Map.of(
                "name", "api-portfolio-java",
                "version", "2.0.0",
                "environment", "Java 21 LTS + Spring Boot 3.3.4",
                "author", "Gabriel Feijó",
                "status", "UP"
        ));
    }
}
