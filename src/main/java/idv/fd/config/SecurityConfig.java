package idv.fd.config;

import idv.fd.security.CustomAuthEntryPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter;

import java.time.Duration;

@Configuration
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

    @Autowired
    private CustomAuthEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {

        return http.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(authenticationEntryPoint))
                .csrf(csrfSpec -> csrfSpec.disable())
                .formLogin(formLoginSpec -> formLoginSpec.disable())
                .httpBasic(httpBasicSpec -> httpBasicSpec.disable())
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchanges -> exchanges.pathMatchers(
                        "/", "/favicon.ico", "/static/**").permitAll()
                        .anyExchange().permitAll())
                .headers(headers -> headers
                                .frameOptions(frameOptions -> frameOptions
                                        .mode(XFrameOptionsServerHttpHeadersWriter.Mode.SAMEORIGIN)
                                )
                                .hsts(hsts -> hsts
                                        .includeSubdomains(true)
                                        .preload(true)
                                        .maxAge(Duration.ofDays(365))
                                )
                                .referrerPolicy(referrer -> referrer
                                        .policy(ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy.SAME_ORIGIN)
                                )
                        //.featurePolicy("geolocation 'self'")
                )
                .build();
    }

}
