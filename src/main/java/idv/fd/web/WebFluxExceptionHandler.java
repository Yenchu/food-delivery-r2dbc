package idv.fd.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import idv.fd.error.AppError;
import idv.fd.error.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.handler.WebFluxResponseStatusExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class WebFluxExceptionHandler extends WebFluxResponseStatusExceptionHandler {

    private ObjectMapper objectMapper;

    public WebFluxExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static Mono<Void> respond(ObjectMapper objectMapper, ServerHttpResponse response, AppError appErr) {

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errMsg = null;
        try {
            errMsg = objectMapper.writeValueAsString(appErr);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }

        if (errMsg != null && errMsg.length() > 0) {
            byte[] bytes = errMsg.getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        }
        return response.setComplete();
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String errLog = String.format("%s %s failed: %s", request.getMethod(), request.getURI().getRawPath(), ex.getMessage());

        // TODO: find a better way to reduce StackTrace error messages
        log.error(errLog, ex.getCause());

        AppError appErr;
        if (ex instanceof AppException) {
            appErr = ((AppException) ex).getError();
        } else {
            appErr = AppError.builder()
                    .msg(ex.getMessage())
                    .build();
        }

        HttpStatus status = null;
        if (appErr.getStatus() != null) {
            status = HttpStatus.resolve(appErr.getStatus());
        }

        if (status != null) {
            response.setStatusCode(status);
        } else {
            if (!setResponseStatus(response, ex)) {
                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return respond(objectMapper, response, appErr);
    }

    private boolean setResponseStatus(ServerHttpResponse response, Throwable ex) {
        boolean result = false;
        int code = determineRawStatusCode(ex);
        if (code != -1) {
            if (response.setRawStatusCode(code)) {
                if (ex instanceof ResponseStatusException) {
                    ((ResponseStatusException) ex).getResponseHeaders()
                            .forEach((name, values) ->
                                    values.forEach(value -> response.getHeaders().add(name, value)));
                }
                result = true;
            }
        } else {
            Throwable cause = ex.getCause();
            if (cause != null) {
                result = setResponseStatus(response, cause);
            }
        }
        return result;
    }
}
