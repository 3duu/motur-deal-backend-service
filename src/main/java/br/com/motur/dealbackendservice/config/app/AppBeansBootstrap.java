package br.com.motur.dealbackendservice.config.app;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import java.util.TimeZone;

@Configuration
public class AppBeansBootstrap {

    private final BeanFactory beanFactory;

    public AppBeansBootstrap(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder mapperBuilder) {
        return mapperBuilder.build().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }


    @Bean
    public OpenAPI customOpenAPI(@Value("${project.version}") String projectVersion) {
        return new OpenAPI().info(new Info()
                .title("Deal Backend Service para o Motur")
                .description("Esse projeto contempla toda a movimentação de anúnicios PJ do Icarros. Sua documentação pode ser encontrada com mais detalhes nesse <a href='https://i-carros.monday.com/docs/3790637649' > <strong>link</strong> </a>  ")
                .version(projectVersion)
                .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

    @PostConstruct
    void timeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
    }

}