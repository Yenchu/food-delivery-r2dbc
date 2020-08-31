package idv.fd.restaurant.repository;

import idv.fd.restaurant.dto.RestaurantInfo;
import idv.fd.restaurant.dto.WeekOpenPeriod;
import io.r2dbc.spi.Row;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;

import java.util.function.Function;

public class OpenHoursCustomRepositoryImpl implements OpenHoursCustomRepository {

    private static final String SELECT_RESTAURANT = "select r.id as restaurantId, r.name as restaurantName"
            + " from open_hours o"
            + " inner join restaurant r on o.restaurant_id = r.id";

    private static final String SELECT_WEEK_OPEN_PERIOD = "select r.id as restaurantId, r.name as restaurantName, sum(o.open_period) as weekOpenPeriod"
            + " from open_hours o"
            + " inner join restaurant r on o.restaurant_id = r.id";

    private static final String findRestaurantsByTime = SELECT_RESTAURANT
            + " where o.open_time <= :time and o.closed_time > :time"
            + " group by restaurantId";

    private static final String findRestaurantsByDayAndTime = SELECT_RESTAURANT
            + " where o.day_of_week = :dayOfWeek and o.open_time <= :time and o.closed_time > :time"
            + " group by restaurantId";

    public static final String findOpenPeriodLessThan = SELECT_RESTAURANT
            + " where o.open_period < :minutes"
            + " group by restaurantId";

    public static final String findOpenPeriodGreaterThan = SELECT_RESTAURANT
            + " where o.open_period > :minutes"
            + " group by restaurantId";

    public static final String findWeekOpenPeriodLessThan = SELECT_WEEK_OPEN_PERIOD
            + " group by restaurantId"
            + " having weekOpenPeriod < :minutes";

    public static final String findWeekOpenPeriodGreaterThan = SELECT_WEEK_OPEN_PERIOD
            + " group by restaurantId"
            + " having weekOpenPeriod > :minutes";

    private DatabaseClient client;

    public OpenHoursCustomRepositoryImpl(DatabaseClient client) {
        this.client = client;
    }

    public Flux<RestaurantInfo> findRestaurantsByTime(int time) {

        return client.execute(findRestaurantsByTime)
                .bind("time", time)
                .map(mapRestaurantInfo())
                .all();
    }

    public Flux<RestaurantInfo> findRestaurantsByDayAndTime(int dayOfWeek, int time) {

        return client.execute(findRestaurantsByDayAndTime)
                .bind("dayOfWeek", dayOfWeek)
                .bind("time", time)
                .map(mapRestaurantInfo())
                .all();
    }

    public Flux<RestaurantInfo> findOpenPeriodLessThan(int minutes) {

        return client.execute(findOpenPeriodLessThan)
                .bind("minutes", minutes)
                .map(mapRestaurantInfo())
                .all();
    }

    public Flux<RestaurantInfo> findOpenPeriodGreaterThan(int minutes) {

        return client.execute(findOpenPeriodGreaterThan)
                .bind("minutes", minutes)
                .map(mapRestaurantInfo())
                .all();
    }

    public Flux<WeekOpenPeriod> findWeekOpenPeriodLessThan(int minutes) {

        return client.execute(findWeekOpenPeriodLessThan)
                .bind("minutes", minutes)
                .map(mapWeekOpenPeriod())
                .all();
    }

    public Flux<WeekOpenPeriod> findWeekOpenPeriodGreaterThan(int minutes) {

        return client.execute(findWeekOpenPeriodGreaterThan)
                .bind("minutes", minutes)
                .map(mapWeekOpenPeriod())
                .all();
    }

    protected Function<Row, RestaurantInfo> mapRestaurantInfo() {

        return row -> RestaurantInfo.builder()
                .restaurantId(row.get("restaurantId", Long.class))
                .restaurantName(row.get("restaurantName", String.class))
                .build();
    }

    protected Function<Row, WeekOpenPeriod> mapWeekOpenPeriod() {

        return row -> WeekOpenPeriod.builder()
                .restaurantId(row.get("restaurantId", Long.class))
                .restaurantName(row.get("restaurantName", String.class))
                .weekOpenPeriod(row.get("weekOpenPeriod", Long.class))
                .build();
    }
}
