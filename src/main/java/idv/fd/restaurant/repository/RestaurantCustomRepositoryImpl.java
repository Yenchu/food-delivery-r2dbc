package idv.fd.restaurant.repository;

import idv.fd.restaurant.dto.RestaurantInfo;
import idv.fd.restaurant.model.Restaurant;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Row;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.function.Function;

public class RestaurantCustomRepositoryImpl implements RestaurantCustomRepository {

    private static final String findRestaurantByIdLocked = "select *"
            + " from restaurant"
            + " where id = :id"
            + " for update";

    private static final String findByNameContaining = "select id as restaurantId, name as restaurantName"
            + " from restaurant"
            + " where name like concat('%', :name, '%')"
            + " order by restaurantName";

    private DatabaseClient client;

    public RestaurantCustomRepositoryImpl(ConnectionFactory connectionFactory) {
        this.client = DatabaseClient.create(connectionFactory);
    }

    public Mono<Restaurant> findRestaurantByIdLocked(Long id) {

        return client.sql(findRestaurantByIdLocked)
                .bind("id", id)
                .map(mapRestaurant())
                .one();
    }

    public Flux<RestaurantInfo> findByNameContaining(String name) {

        return client.sql(findByNameContaining)
                .bind("name", name)
                .map(mapRestaurantInfo())
                .all();
    }

    protected Function<Row, Restaurant> mapRestaurant() {

        return row -> Restaurant.builder()
                .id(row.get("id", Long.class))
                .name(row.get("name", String.class))
                .cashBalance(row.get("cash_balance", BigDecimal.class))
                .build();
    }

    protected Function<Row, RestaurantInfo> mapRestaurantInfo() {

        return row -> RestaurantInfo.builder()
                .restaurantId(row.get("restaurantId", Long.class))
                .restaurantName(row.get("restaurantName", String.class))
                .build();
    }
}
