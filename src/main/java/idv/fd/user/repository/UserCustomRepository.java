package idv.fd.user.repository;

import idv.fd.user.model.User;
import reactor.core.publisher.Mono;

public interface UserCustomRepository {

    Mono<User> createUser(User user);

    Mono<User> findUserByIdLocked(Long id);

}
