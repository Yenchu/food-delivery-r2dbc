package idv.fd.restaurant.repository;

import idv.fd.restaurant.model.Restaurant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface RestaurantRepository extends ReactiveSortingRepository<Restaurant, Long>, RestaurantCustomRepository {

    Flux<Restaurant> findAllByOrderByName(Pageable pageable);

    //Flux<Restaurant> findByNameContainingOrderByName(String name);

}
