package com.sicredi.assemblyVotingApi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Assembly Voting API")
                        .description("API para gerenciamento de votações em assembleias")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Vitor Macedo Silva")
                                .url("https://github.com/vitormasi/assembly-voting-api")
                                .email("vitormasi@gmail.com")));
    }

    @Bean
    public GroupedOpenApi defaultApi() {
        return GroupedOpenApi.builder()
                .group("API Original")
                .pathsToExclude(ApiVersionConstants.V2 + "/**")
                .build();
    }

    @Bean
    public GroupedOpenApi v2Api() {
        return GroupedOpenApi.builder()
                .group("v2 - API Versionada")
                .pathsToMatch(ApiVersionConstants.V2 + "/**")
                .build();
    }
}