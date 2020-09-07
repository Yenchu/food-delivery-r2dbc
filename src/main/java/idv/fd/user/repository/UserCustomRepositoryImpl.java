package idv.fd.user.repository;

import idv.fd.user.model.User;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Row;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.function.Function;

public class UserCustomRepositoryImpl implements UserCustomRepository {

    private static final String findUserByIdLocked = "select *"
            + " from app_user"
            + " where id = :id"
            + " for update";

    private DatabaseClient client;

    private R2dbcEntityTemplate template;

    public UserCustomRepositoryImpl(ConnectionFactory connectionFactory, R2dbcEntityTemplate template) {
        this.client = DatabaseClient.create(connectionFactory);
        this.template = template;
    }

    /**
     * Currently R2DBC repository save() doesn't support self-assigned @Id.
     * @param user
     * @return
     */
    public Mono<User> createUser(User user) {
        return template.insert(user);
    }

    public Mono<User> findUserByIdLocked(Long id) {

        return client.sql(findUserByIdLocked)
                .bind("id", id)
                .map(mapUser())
                .one();
    }

    protected Function<Row, User> mapUser() {

        return row -> User.builder()
                .id(row.get("id", Long.class))
                .name(row.get("name", String.class))
                .cashBalance(row.get("cash_balance", BigDecimal.class))
                .build();
    }
}
