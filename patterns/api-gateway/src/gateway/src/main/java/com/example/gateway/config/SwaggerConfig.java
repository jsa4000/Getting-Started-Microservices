package com.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public UiConfiguration uiConfiguration() {
        return UiConfigurationBuilder.builder()
                .deepLinking(true)
                .docExpansion(DocExpansion.NONE)
                .operationsSorter(OperationsSorter.ALPHA)
                .defaultModelRendering(ModelRendering.MODEL)
                .build();
    }

    @Bean
    public Docket apiDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .paths(PathSelectors.regex("(?!/error).+"))
                .paths(PathSelectors.regex("(?!/actuator).+"))
                .build()
                .apiInfo(apiInfo())
                .securitySchemes(Arrays.asList(securityApiKey(), securityOAuth()))
                .securityContexts(Arrays.asList(securityContext()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("OAuth API")
                .version("1.0.0")
                .license("(C) Copyright jsa4000")
                .description("API functionality for OAuth Service.")
                .contact(new Contact("Javier Santos", "www.jsa400.com", "jsa4000@gmail.com"))
                .build();
    }

    @Bean
    public SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder()
                .scopeSeparator(",")
                .additionalQueryStringParams(null)
                .useBasicAuthenticationWithAccessCodeGrant(false)
                .build();
    }

    private SecurityScheme securityApiKey() {
        return new ApiKey("Bearer", "Authorization", "header");
    }

    private OAuth securityOAuth() {
        GrantType creGrant = new ResourceOwnerPasswordCredentialsGrant("/oauth/token");
        return new OAuth("OAuth", scopes(), Arrays.asList(creGrant));
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.any())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope[] authorizationScopes ={ globalScope() };
        return Arrays.asList(new SecurityReference("Bearer", authorizationScopes));
    }

    private AuthorizationScope globalScope() {
        return new AuthorizationScope("global", "accessEverything");
    }

    private List<AuthorizationScope> scopes() {
        return Arrays.asList(
                new AuthorizationScope("read", "for read operations"),
                new AuthorizationScope("trust", "for trust operations"),
                new AuthorizationScope("write", "for write operations"));
    }

}

