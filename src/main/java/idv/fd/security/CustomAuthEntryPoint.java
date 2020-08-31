package idv.fd.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import idv.fd.config.SecurityConfig;
import idv.fd.error.AppError;
import idv.fd.web.WebFluxExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomAuthEntryPoint implements ServerAuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthEntryPoint.class);

    private ObjectMapper objectMapper;

    public CustomAuthEntryPoint(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String errLog = String.format("%s %s failed: %s", request.getMethod(), request.getURI().getRawPath(), ex.getMessage());

        log.error(errLog, ex);

        AppError appErr = AppError.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .msg(ex.getMessage())
                .build();

        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        return WebFluxExceptionHandler.respond(objectMapper, response, appErr);
    }
}
