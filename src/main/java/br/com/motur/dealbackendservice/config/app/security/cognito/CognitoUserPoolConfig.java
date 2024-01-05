package br.com.motur.dealbackendservice.config.app.security.cognito;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
@Getter
public class CognitoUserPoolConfig {

    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;
    @Value("${aws.cognito.clientId}")
    private String clientId;
    @Value("${aws.cognito.clientSecret}")
    private String clientSecret;
    @Value("${aws.cognito.region}")
    private String region;
    @Value("${aws.cognito.url}")
    private String url;

    @Bean
    public AWSCognitoIdentityProvider awsCognitoIdentityProvider() {
        return AWSCognitoIdentityProviderClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("", "")))
                .withRegion(Regions.valueOf(region.replace("-", "_")))
                .build();
    }
}
