package idv.fd.config;

import idv.fd.restaurant.repository.RestaurantRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.r2dbc.core.DatabaseClient;

@TestConfiguration
public class TestConfig {

    @Bean
    public RestaurantTestService restaurantTestService(RestaurantRepository restaurantRepository, DatabaseClient databaseClient) {

        return new RestaurantTestService(restaurantRepository, databaseClient);
    }
}
