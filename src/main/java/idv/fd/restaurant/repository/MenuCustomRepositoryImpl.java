package idv.fd.restaurant.repository;

import idv.fd.restaurant.dto.DishInfo;
import idv.fd.restaurant.dto.DishNumb;
import idv.fd.restaurant.model.Menu;
import io.r2dbc.spi.Row;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.function.Function;

public class MenuCustomRepositoryImpl implements MenuCustomRepository {

    private static final String findMenuByIdLocked = "select *"
            + " from menu"
            + " where id = :id"
            + " for update";

    private static final String SELECT_DISH_INFO = "select r.id as restaurantId, r.name as restaurantName, m.id as menuId, m.dish_name dishName, m.price as price"
            + " from menu m"
            + " inner join restaurant r on m.restaurant_id = r.id";

    private static final String SELECT_DISH_NUMB = "select r.id as restaurantId, r.name as restaurantName, count(m.id) as dishNumb"
            + " from menu m"
            + " inner join restaurant r on m.restaurant_id = r.id";

    private static final String findByDishNameContaining = SELECT_DISH_INFO
            + " where m.dish_name like concat('%', :dishName, '%')"
            + " order by dishName";

    private static final String findByPriceGreaterThanEqualAndPriceLessThanEqual = SELECT_DISH_INFO
            + " where m.price >= :minPrice and m.price <= :maxPrice"
            + " order by :sortField";

    private static final String findByDishesLessThan = SELECT_DISH_NUMB
            + " group by restaurantId"
            + " having count(m.id) < :dishNumb"
            + " order by dishNumb";

    private static final String findByDishesGreaterThan = SELECT_DISH_NUMB
            + " group by restaurantId"
            + " having count(m.id) > :dishNumb"
            + " order by dishNumb";

    private static final String findByDishesLessThanAndWithinPrices = SELECT_DISH_NUMB
            + " where m.price >= :minPrice and m.price <= :maxPrice"
            + " group by restaurantId"
            + " having count(m.id) < :dishNumb"
            + " order by dishNumb";

    private static final String findByDishesGreaterThanAndWithinPrices = SELECT_DISH_NUMB
            + " where m.price >= :minPrice and m.price <= :maxPrice"
            + " group by restaurantId"
            + " having count(m.id) > :dishNumb"
            + " order by dishNumb";

    private DatabaseClient client;

    public MenuCustomRepositoryImpl(DatabaseClient client) {
        this.client = client;
    }

    public Mono<Menu> findMenuByIdLocked(Long id) {

        return client.execute(findMenuByIdLocked)
                .bind("id", id)
                .as(Menu.class)
                .fetch()
                .one();
    }

    public Flux<DishInfo> findByDishNameContaining(String dishName) {

        return client.execute(findByDishNameContaining)
                .bind("dishName", dishName)
                .map(mapDishInfo())
                .all();
    }

    public Flux<DishInfo> findByPriceGreaterThanEqualAndPriceLessThanEqual(
            BigDecimal minPrice, BigDecimal maxPrice, String sortField) {

        return client.execute(findByPriceGreaterThanEqualAndPriceLessThanEqual)
                .bind("minPrice", minPrice)
                .bind("maxPrice", maxPrice)
                .bind("sortField", sortField)
                .map(mapDishInfo())
                .all();
    }

    public Flux<DishNumb> findByDishesLessThan(int dishNumb) {

        return client.execute(findByDishesLessThan)
                .bind("dishNumb", dishNumb)
                .map(mapDishNumb())
                .all();
    }


    public Flux<DishNumb> findByDishesGreaterThan(int dishNumb) {

        return client.execute(findByDishesGreaterThan)
                .bind("dishNumb", dishNumb)
                .map(mapDishNumb())
                .all();
    }

    public Flux<DishNumb> findByDishesLessThanAndWithinPrices(int dishNumb, BigDecimal minPrice, BigDecimal maxPrice) {

        return client.execute(findByDishesLessThanAndWithinPrices)
                .bind("minPrice", minPrice)
                .bind("maxPrice", maxPrice)
                .bind("dishNumb", dishNumb)
                .map(mapDishNumb())
                .all();
    }

    public Flux<DishNumb> findByDishesGreaterThanAndWithinPrices(int dishNumb, BigDecimal minPrice, BigDecimal maxPrice) {

        return client.execute(findByDishesGreaterThanAndWithinPrices)
                .bind("minPrice", minPrice)
                .bind("maxPrice", maxPrice)
                .bind("dishNumb", dishNumb)
                .map(mapDishNumb())
                .all();
    }

    protected Function<Row, DishInfo> mapDishInfo() {

        return row -> DishInfo.builder()
                .restaurantId(row.get("restaurantId", Long.class))
                .restaurantName(row.get("restaurantName", String.class))
                .menuId(row.get("menuId", Long.class))
                .dishName(row.get("dishName", String.class))
                .price(row.get("price", BigDecimal.class))
                .build();
    }

    protected Function<Row, DishNumb> mapDishNumb() {

        return row -> DishNumb.builder()
                .restaurantId(row.get("restaurantId", Long.class))
                .restaurantName(row.get("restaurantName", String.class))
                .dishNumb(row.get("dishNumb", Long.class))
                .build();
    }

}
