package br.com.gabrielfeijo.portfolio.infrastructure.web.controller;

import br.com.gabrielfeijo.portfolio.application.dto.request.CreateReviewRequest;
import br.com.gabrielfeijo.portfolio.application.dto.response.ReviewResponse;
import br.com.gabrielfeijo.portfolio.application.service.ReviewService;
import br.com.gabrielfeijo.portfolio.domain.repository.CommandRepositoryPort;
import br.com.gabrielfeijo.portfolio.domain.repository.ContactRepositoryPort;
import br.com.gabrielfeijo.portfolio.domain.repository.ReviewRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private CommandRepositoryPort commandRepositoryPort;

    @MockBean
    private ReviewRepositoryPort reviewRepositoryPort;

    @MockBean
    private ContactRepositoryPort contactRepositoryPort;

    @Test
    @DisplayName("GET /v2/review - Deve listar reviews publicamente")
    void shouldListReviewsPublicly() throws Exception {
        ReviewResponse rev = new ReviewResponse("rev-1", "John", "Muito bom!", 5, Instant.now(), Instant.now());
        when(reviewService.getReviews(any())).thenReturn(List.of(rev));

        mockMvc.perform(get("/v2/review"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("John"))
                .andExpect(jsonPath("$[0].stars").value(5));
    }

    @Test
    @DisplayName("POST /v2/review - Deve permitir criação pública de review com payload válido")
    void shouldCreateReviewPublicly() throws Exception {
        CreateReviewRequest request = new CreateReviewRequest("John Doe", "Excelente portfólio e projetos!", 5);
        ReviewResponse rev = new ReviewResponse("rev-1", "John Doe", "Excelente portfólio e projetos!", 5, Instant.now(), Instant.now());
        when(reviewService.createReview(any())).thenReturn(rev);

        mockMvc.perform(post("/v2/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("John Doe"));
    }

    @Test
    @DisplayName("POST /v2/review - Deve retornar 400 Bad Request se comentário for curto ou estrelas inválidas")
    void shouldReturn400WhenValidationFails() throws Exception {
        CreateReviewRequest request = new CreateReviewRequest("J", "Curto", 10);

        mockMvc.perform(post("/v2/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400));
    }
}
