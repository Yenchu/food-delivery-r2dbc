package idv.fd.restaurant.repository;

import idv.fd.restaurant.dto.RestaurantInfo;
import idv.fd.restaurant.model.Restaurant;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RestaurantCustomRepository {

    Mono<Restaurant> findRestaurantByIdLocked(Long id);

    Flux<RestaurantInfo> findByNameContaining(String name);

}
