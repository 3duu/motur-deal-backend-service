package br.com.motur.dealbackendservice.config.app.security.cognito;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CognitoService {

    private AWSCognitoIdentityProvider cognitoClient;

    public CognitoService() {
        this.cognitoClient = AWSCognitoIdentityProviderClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    public String signUpUser(String username, String password, String email) {
        SignUpRequest signUpRequest = new SignUpRequest()
                .withClientId("motur-login") // Substitua pelo seu App Client ID
                //.withUsername(username)
                .withPassword(password)
                .withUserAttributes(
                        new AttributeType()
                                .withName("email")
                                .withValue(email)
                );

        SignUpResult result = cognitoClient.signUp(signUpRequest);
        return result.getUserSub();
    }

    public String loginUser(final String username, final String password) {
        AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest()
                .withClientId("motur-login") // Substitua pelo seu App Client ID
                .withUserPoolId("us-east-1_edAonTQSm") // Substitua pelo seu User Pool ID
                .withAuthFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .withAuthParameters(
                        Map.of(
                                "USERNAME", username,
                                "PASSWORD", password
                        )
                );

        AdminInitiateAuthResult result = cognitoClient.adminInitiateAuth(authRequest);
        return result.getAuthenticationResult().getAccessToken();
    }

    public void resetPassword(String username) {
        AdminResetUserPasswordRequest resetRequest = new AdminResetUserPasswordRequest()
                .withUserPoolId("us-east-1_edAonTQSm") // Substitua pelo seu User Pool ID
                .withUsername(username);

        cognitoClient.adminResetUserPassword(resetRequest);
    }

}
