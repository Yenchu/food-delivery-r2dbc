package idv.fd.user.repository;

import idv.fd.user.model.Favourite;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface FavouriteRepository extends ReactiveCrudRepository<Favourite, Long> {

    Flux<Favourite> findByUserId(Long userId);

}
