package br.com.motur.dealbackendservice.config.app.security.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.GetUserRequest;
import com.amazonaws.services.cognitoidp.model.GetUserResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CognitoUserDetailsService implements UserDetailsService {

    @Autowired
    private AWSCognitoIdentityProvider awsCognitoIdentityProvider;

    @Autowired
    private CognitoUserPoolConfig cognitoUserPoolConfig;

    public UserDetails loadUserByJwt(Jwt jwt) {
        String username = jwt.getSubject();
        GetUserRequest getUserRequest = new GetUserRequest()
                .withAccessToken(jwt.getTokenValue());
        GetUserResult getUserResult = awsCognitoIdentityProvider.getUser(getUserRequest);
        List<AttributeType> attributes = getUserResult.getUserAttributes();
        Map<String, String> attributeMap = attributes.stream()
                .collect(Collectors.toMap(AttributeType::getName, AttributeType::getValue));
        List<GrantedAuthority> authorities = getAuthorities(attributeMap);
        return new User(username, "", authorities);
    }
    private List<GrantedAuthority> getAuthorities(Map<String, String> attributeMap) {
        String groupAttributeName = "custom:groups";
        String groups = attributeMap.getOrDefault(groupAttributeName, "");
        return Arrays.stream(groups.split(","))
                .map(group -> new SimpleGrantedAuthority("ROLE_" + group.toUpperCase()))
                .collect(Collectors.toList());
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        throw new UnsupportedOperationException("This method is not supported.");
    }
}
