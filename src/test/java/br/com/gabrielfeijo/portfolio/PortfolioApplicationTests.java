package br.com.gabrielfeijo.portfolio;

import br.com.gabrielfeijo.portfolio.domain.repository.CommandRepositoryPort;
import br.com.gabrielfeijo.portfolio.domain.repository.ContactRepositoryPort;
import br.com.gabrielfeijo.portfolio.domain.repository.ReviewRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration"
})
@ActiveProfiles("test")
class PortfolioApplicationTests {

    @MockBean
    private CommandRepositoryPort commandRepositoryPort;

    @MockBean
    private ReviewRepositoryPort reviewRepositoryPort;

    @MockBean
    private ContactRepositoryPort contactRepositoryPort;

    @Test
    void contextLoads() {
    }
}
