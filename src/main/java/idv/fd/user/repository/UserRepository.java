package idv.fd.user.repository;

import idv.fd.user.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveSortingRepository<User, Long>, UserCustomRepository {

    Flux<User> findAllByOrderByName(Pageable pageable);

}
