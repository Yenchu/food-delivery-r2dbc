package idv.fd.config;

import idv.fd.restaurant.repository.RestaurantRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public RestaurantTestService restaurantTestService(RestaurantRepository restaurantRepository) {

        return new RestaurantTestService(restaurantRepository);
    }
}
