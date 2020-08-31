package idv.fd.user.repository;

import idv.fd.user.model.User;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

public class UserCustomRepositoryImpl implements UserCustomRepository {

    private static final String findUserByIdLocked = "select *"
            + " from app_user"
            + " where id = :id"
            + " for update";

    private DatabaseClient client;

    public UserCustomRepositoryImpl(DatabaseClient client) {
        this.client = client;
    }

    public Mono<User> findUserByIdLocked(Long id) {

        return client.execute(findUserByIdLocked)
                .bind("id", id)
                .as(User.class)
                .fetch()
                .one();
    }
}
