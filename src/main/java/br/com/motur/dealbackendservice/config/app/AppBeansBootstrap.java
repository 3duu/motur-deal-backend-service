package br.com.motur.dealbackendservice.config.app;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.TimeZone;

/**
 * This class is responsible for configuring beans that will be used in the application.
 */
@Configuration
public class AppBeansBootstrap {

    /**
     * Configures the ObjectMapper bean.
     * @param mapperBuilder Jackson2ObjectMapperBuilder instance.
     * @return ObjectMapper instance with serialization inclusion set to NON_NULL.
     */
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder mapperBuilder) {
        return mapperBuilder.build().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * Configures the ModelMapper bean.
     * @return ModelMapper instance.
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * Configures the RestTemplate bean.
     * @return RestTemplate instance.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Configures the OpenAPI bean.
     * @param projectVersion The version of the project.
     * @return OpenAPI instance with the project's information and security requirements.
     */
    @Bean
    public OpenAPI customOpenAPI(@Value("${project.version}") final String projectVersion) {

        final String bearerAuth = "bearerAuth";
        return new OpenAPI().info(new Info()
                        .title("Deal Backend Service para o Motur")
                        .description("Esse projeto contempla toda a movimentação de anúnicios PJ do Icarros. Sua documentação pode ser encontrada com mais detalhes nesse <a href='https://i-carros.monday.com/docs/3790637649' > <strong>link</strong> </a>  ")
                        .version(projectVersion)
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList(bearerAuth))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(bearerAuth, new SecurityScheme()
                                .name(bearerAuth)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    /**
     * Sets the default time zone to "America/Sao_Paulo" after the bean properties have been set.
     */
    @PostConstruct
    void timeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
    }

}