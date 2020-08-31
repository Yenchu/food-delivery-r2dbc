package idv.fd.restaurant.repository;

import idv.fd.restaurant.dto.RestaurantInfo;
import idv.fd.restaurant.model.Restaurant;
import io.r2dbc.spi.Row;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    public RestaurantCustomRepositoryImpl(DatabaseClient client) {
        this.client = client;
    }

    public Mono<Restaurant> findRestaurantByIdLocked(Long id) {

        return client.execute(findRestaurantByIdLocked)
                .bind("id", id)
                .as(Restaurant.class)
                .fetch()
                .one();
    }

    public Flux<RestaurantInfo> findByNameContaining(String name) {

        return client.execute(findByNameContaining)
                .bind("name", name)
                .map(mapRestaurantInfo())
                .all();
    }

    protected Function<Row, RestaurantInfo> mapRestaurantInfo() {

        return row -> RestaurantInfo.builder()
                .restaurantId(row.get("restaurantId", Long.class))
                .restaurantName(row.get("restaurantName", String.class))
                .build();
    }
}
