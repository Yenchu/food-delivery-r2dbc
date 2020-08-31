package idv.fd.etl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import idv.fd.etl.dto.RestaurantVo;
import idv.fd.etl.dto.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Component
@Slf4j
public class RawDataExtractor {

    private String restaurantDataUrl;

    private String userDataUrl;

    private WebClient webClient;

    public RawDataExtractor(
            @Value("${RESTAURANT_DATA_URL}") String restaurantDataUrl,
            @Value("${USER_DATA_URL}") String userDataUrl,
            WebClient.Builder webClientBuilder) {

        this.restaurantDataUrl = restaurantDataUrl;

        this.userDataUrl = userDataUrl;

        this.webClient = webClientBuilder
                .codecs(configurer -> {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    configurer.customCodecs()
                            .register(new Jackson2JsonDecoder(mapper, MimeTypeUtils.parseMimeType(MediaType.TEXT_PLAIN_VALUE)));
                })
                .build();
    }

    public Flux<RestaurantVo> extractRestaurantData() {

        return webClient.get()
                .uri(restaurantDataUrl)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    log.error("get {} failed: {}", restaurantDataUrl, response.statusCode());
                    return response.createException();
                })
                .bodyToFlux(RestaurantVo.class)
                .timeout(Duration.ofMillis(10000));
    }

    public Flux<UserVo> extractUserData() {

        return webClient.get()
                .uri(userDataUrl)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    log.error("get {} failed: {}", userDataUrl, response.statusCode());
                    return response.createException();
                })
                .bodyToFlux(UserVo.class)
                .timeout(Duration.ofMillis(10000));
    }
}
