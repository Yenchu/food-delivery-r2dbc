package idv.fd.config;

import idv.fd.restaurant.dto.EditRestaurant;
import idv.fd.restaurant.model.Restaurant;
import idv.fd.restaurant.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Slf4j
public class RestaurantTestService {

    private RestaurantRepository restaurantRepository;

    private DatabaseClient databaseClient;

    public RestaurantTestService(RestaurantRepository restaurantRepository, DatabaseClient databaseClient) {
        this.restaurantRepository = restaurantRepository;
        this.databaseClient = databaseClient;
    }

    @Transactional
    public Mono<Restaurant> updateRestaurant(EditRestaurant editRest) {

        return restaurantRepository.findById(editRest.getRestaurantId()).flatMap(r -> {
            log.debug("** update restaurant");
            r.setName(editRest.getRestaurantName());
            return restaurantRepository.save(r);
        });
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Mono<Restaurant> updateRestaurantSleepy(EditRestaurant editRest) {

        return restaurantRepository.findRestaurantByIdLocked(editRest.getRestaurantId()).flatMap(r -> {
            log.debug("@@ update & lock restaurant");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            r.setName(editRest.getRestaurantName());
            return restaurantRepository.save(r);
        });
    }
}
