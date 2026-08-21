package br.com.gabrielfeijo.portfolio.infrastructure.web.controller;

import br.com.gabrielfeijo.portfolio.application.dto.response.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "Root", description = "Endpoints raiz e verificação detalhada de integridade")
@RequiredArgsConstructor
public class RootController {

    @Autowired(required = false)
    private MongoTemplate mongoTemplate;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @GetMapping(value = {"/", "/v2", "/v2/", "/v2/health", "/health"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Health check detalhado e status do ecossistema",
            description = "Retorna o status operacional da API, integridade e latência do MongoDB, telemetria da JVM e uptime.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ecossistema operacional"),
                    @ApiResponse(responseCode = "503", description = "Serviço ou banco de dados indisponível")
            }
    )
    public ResponseEntity<HealthResponse> getHealth() {
        long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();
        String formattedUptime = formatUptime(uptimeMillis);

        Map<String, Object> appMetadata = new LinkedHashMap<>();
        appMetadata.put("name", "api-portfolio-java");
        appMetadata.put("version", "2.0.0");
        appMetadata.put("environment", activeProfile);
        appMetadata.put("author", "Gabriel Feijó");
        appMetadata.put("documentation", "/swagger");

        Map<String, Object> components = new LinkedHashMap<>();
        boolean isMongoHealthy = checkMongoHealth(components);

        Map<String, Object> systemMetrics = getSystemMetrics();

        String overallStatus = isMongoHealthy ? "UP" : "DEGRADED";
        HttpStatus httpStatus = isMongoHealthy ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;

        HealthResponse healthResponse = new HealthResponse(
                overallStatus,
                Instant.now(),
                formattedUptime,
                appMetadata,
                components,
                systemMetrics
        );

        return ResponseEntity.status(httpStatus).body(healthResponse);
    }

    private boolean checkMongoHealth(Map<String, Object> components) {
        if (mongoTemplate == null) {
            components.put("database", Map.of(
                    "type", "MongoDB",
                    "status", "MOCKED / TEST_MODE"
            ));
            return true;
        }

        try {
            long start = System.currentTimeMillis();
            Document pingResult = mongoTemplate.executeCommand(new Document("ping", 1));
            long latencyMs = System.currentTimeMillis() - start;

            components.put("database", Map.of(
                    "type", "MongoDB",
                    "status", "UP",
                    "latencyMs", latencyMs,
                    "databaseName", mongoTemplate.getDb().getName()
            ));
            return true;
        } catch (Exception e) {
            log.error("MongoDB health check failed: {}", e.getMessage());
            components.put("database", Map.of(
                    "type", "MongoDB",
                    "status", "DOWN",
                    "error", e.getMessage()
            ));
            return false;
        }
    }

    private Map<String, Object> getSystemMetrics() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemoryMb = runtime.maxMemory() / (1024 * 1024);
        long totalMemoryMb = runtime.totalMemory() / (1024 * 1024);
        long freeMemoryMb = runtime.freeMemory() / (1024 * 1024);
        long usedMemoryMb = totalMemoryMb - freeMemoryMb;

        Map<String, Object> system = new LinkedHashMap<>();
        system.put("javaVersion", System.getProperty("java.version"));
        system.put("virtualThreads", true);
        system.put("availableProcessors", runtime.availableProcessors());
        system.put("usedMemoryMb", usedMemoryMb);
        system.put("freeMemoryMb", freeMemoryMb);
        system.put("totalMemoryMb", totalMemoryMb);
        system.put("maxMemoryMb", maxMemoryMb);

        return system;
    }

    private String formatUptime(long millis) {
        Duration duration = Duration.ofMillis(millis);
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        if (days > 0) {
            return String.format("%dd %02dh %02dm %02ds", days, hours, minutes, seconds);
        }
        if (hours > 0) {
            return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
        }
        return String.format("%02dm %02ds", minutes, seconds);
    }
}
