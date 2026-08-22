package br.com.gabrielfeijo.portfolio.infrastructure.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Portfolio API - Documentação (Java 21 & Spring Boot 3)")
                        .description("API backend de alta performance desenvolvida em Java 21 + Spring Boot 3 para o portfólio interativo acessível em https://gabrielfeijo.com.br/.")
                        .version("2.0.0")
                        .contact(new Contact()
                                .name("Gabriel Feijó")
                                .url("https://gabrielfeijo.com.br")
                                .email("feijo801@gmail.com"))
                        .license(new License().name("Apache 2.0").url("https://spring.io")))
                .tags(List.of(
                        new Tag().name("Command").description("Endpoints para gerenciamento e execução de comandos do terminal virtual"),
                        new Tag().name("Review").description("Endpoints para envio e gerenciamento de reviews/depoimentos"),
                        new Tag().name("Contact").description("Endpoints para recebimento de mensagens do formulário de contato"),
                        new Tag().name("Health").description("Endpoints de verificação de integridade e status operacional")
                ))
                .components(new Components()
                        .addSecuritySchemes("api-key", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .name("x-api-key")
                                .in(SecurityScheme.In.HEADER)
                                .description("Chave de autenticação administrativa (header x-api-key)")))
                .addSecurityItem(new SecurityRequirement().addList("api-key"));
    }
}
