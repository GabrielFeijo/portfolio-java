package br.com.gabrielfeijo.portfolio.infrastructure.web.filter;

import br.com.gabrielfeijo.portfolio.application.dto.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        if (path.startsWith("/swagger") || path.startsWith("/v2/api-docs") || path.startsWith("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);
        String bucketKey = clientIp + ":" + method + ":" + resolveRateLimitGroup(path, method);

        Bucket bucket = buckets.computeIfAbsent(bucketKey, k -> createNewBucket(path, method));

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.TOO_MANY_REQUESTS.value(),
                    Instant.now(),
                    request.getRequestURI(),
                    request.getMethod(),
                    "ThrottlerException: Too Many Requests",
                    "Too Many Requests"
            );

            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }

    private String resolveRateLimitGroup(String path, String method) {
        if (path.startsWith("/v2/contact") && "POST".equalsIgnoreCase(method)) {
            return "CONTACT_POST";
        }
        if (path.startsWith("/v2/review") && "POST".equalsIgnoreCase(method)) {
            return "REVIEW_POST";
        }
        if (path.startsWith("/v2/command") && "POST".equalsIgnoreCase(method)) {
            return "COMMAND_POST";
        }
        return "GENERAL";
    }

    private Bucket createNewBucket(String path, String method) {
        if (path.startsWith("/v2/contact") && "POST".equalsIgnoreCase(method)) {
            Refill refill = Refill.intervally(2, Duration.ofSeconds(10));
            Bandwidth limit = Bandwidth.classic(2, refill);
            return Bucket.builder().addLimit(limit).build();
        }

        if (path.startsWith("/v2/review") && "POST".equalsIgnoreCase(method)) {
            Refill refill = Refill.intervally(3, Duration.ofSeconds(1));
            Bandwidth limit = Bandwidth.classic(3, refill);
            return Bucket.builder().addLimit(limit).build();
        }

        if (path.startsWith("/v2/command") && ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method))) {
            Refill refill = Refill.intervally(5, Duration.ofSeconds(1));
            Bandwidth limit = Bandwidth.classic(5, refill);
            return Bucket.builder().addLimit(limit).build();
        }

        Refill refill = Refill.intervally(100, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(100, refill);
        return Bucket.builder().addLimit(limit).build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
