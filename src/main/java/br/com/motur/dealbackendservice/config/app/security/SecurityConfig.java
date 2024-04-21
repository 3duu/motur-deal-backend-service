package br.com.motur.dealbackendservice.config.app.security;

/*import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;*/
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

/*
    private final CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler;

    private final CustomBearerTokenAuthenticationEntryPoint customCognitoAuthenticationEntryPoint;

    private final CognitoUserPoolConfig cognitoUserPoolConfig;*/
/*
    @Autowired
    public SecurityConfig(CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler, CustomBearerTokenAuthenticationEntryPoint customCognitoAuthenticationEntryPoint, CognitoUserPoolConfig cognitoUserPoolConfig) {
        this.customBearerTokenAccessDeniedHandler = customBearerTokenAccessDeniedHandler;
        this.customCognitoAuthenticationEntryPoint = customCognitoAuthenticationEntryPoint;
        this.cognitoUserPoolConfig = cognitoUserPoolConfig;
    }*/
/*
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/v1/login/**").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2ResourceServer ->
                        oauth2ResourceServer
                                .jwt(jwt ->
                                        //jwt.decoder(JwtDecoders.fromOidcIssuerLocation("https://accounts-motur.auth." + cognitoUserPoolConfig.getRegion().toLowerCase() +".amazoncognito.com"))
                                        jwt.decoder(JwtDecoders.fromOidcIssuerLocation("https://cognito-idp.{region}.amazonaws.com/{userPoolId}".replace("{region}", cognitoUserPoolConfig.getRegion().toLowerCase()).replace("{userPoolId}", cognitoUserPoolConfig.getUserPoolId())))
                                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                                )
                );
        return http.build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName("cognito:groups");
        converter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
        return jwtConverter;
    }*/

    /*@Bean
    public KeycloakConfigResolver KeycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }*/

    /*httpSecurityCorsConfigurer -> {
                    httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
                }*/

    /**
     * Configuração de segurança
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /*return http
                .authorizeHttpRequests(ar ->
                        ar
                            .requestMatchers(AUTH_WHITELIST).permitAll()
                            .anyRequest().authenticated()
                            //.anyRequest().permitAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwtAuthenticationConverter()))
                .csrf(csrf -> csrf.disable())
                .cors(crs -> crs.configurationSource(corsConfigurationSource()))
                .build();*/

        return http
                .authorizeHttpRequests(ar ->
                                ar
                                        .requestMatchers(AUTH_WHITELIST).permitAll()
                                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable())
                .cors(crs -> crs.configurationSource(corsConfigurationSource()))
                .build();
    }

    /***
     * Configuração de CORS
     * @return
     */

    private CorsConfigurationSource corsConfigurationSource() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }


    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtConverter;
    }

    /*@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        final KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }*/

    /*@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }*/

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    private static final String[] AUTH_WHITELIST = {
            // -- Swagger UI v2
            "/v2/api-docs",
            "v2/api-docs",
            "/swagger-resources",
            "swagger-resources",
            "/swagger-resources/**",
            "swagger-resources/**",
            "/configuration/ui",
            "configuration/ui",
            "/configuration/security",
            "configuration/security",
            "/swagger-ui.html",
            "swagger-ui.html",
            "api/swagger-ui.html",
            "/api/swagger-ui.html",
            "webjars/**",
            // -- Swagger UI v3
            "/v3/api-docs/**",
            "v3/api-docs/**",
            "/swagger-ui/**",
            "swagger-ui/**",
            "api/swagger-ui/index.html",
            "/api/swagger-ui/index.html",
            "api/swagger-ui/**",
            "/api/swagger-ui/**",
            // CSA Controllers
            "/csa/api/token",
            // Actuators
            "/actuator/**",
            "/health/**",
            //endpoints
            "/api/v1/login/**",
            "/api/v1/public/**",
            "api/v1/login/**",
            "api/v1/public/**",
            "/v1/login/**",
            "/v1/public/**",
            "api-docs/**",
            "/api-docs/**",
    };

}
