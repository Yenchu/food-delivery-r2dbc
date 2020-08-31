package idv.fd.purchase.repository;

import idv.fd.purchase.dto.Count;
import idv.fd.purchase.dto.RestaurantTxAmount;
import idv.fd.purchase.dto.TxNumbAmount;
import idv.fd.purchase.dto.UserTxAmount;
import io.r2dbc.spi.Row;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.Function;

public class PurchaseHistoryCustomRepositoryImpl implements PurchaseHistoryCustomRepository {

    private static final String COUNT_TX_AMOUNT = "select count(*) as numb"
            + " from (select user_id, sum(transaction_amount) as txSum"
            + " from purchase_history"
            + " where transaction_date between :fromDate and :toDate"
            + " group by user_id) s";

    private static final String findTopTxUsers = "select u.id as userId, u.name as userName, sum(p.transaction_amount) as txAmount"
            + " from purchase_history p"
            + " inner join app_user u on p.user_id = u.id"
            + " where p.transaction_date between :fromDate and :toDate"
            + " group by userId"
            + " order by txAmount desc"
            + " limit :top";

    private static final String findTxNumbAmount = "select count(p.id) as txNumb, sum(p.transaction_amount) as txAmount"
            + " from purchase_history p"
            + " where p.transaction_date between :fromDate and :toDate";

    private static final String findMaxTxNumbRestaurants = "select restaurant_id as restaurantId, restaurant_name as restaurantName, count(id) as txNumb"
            + " from purchase_history"
            + " group by restaurantId, restaurantName"
            + " having count(id) ="
            + " (select max(c.txCount) from (select restaurant_id, count(id) as txCount from purchase_history group by restaurant_id) c)";

    private static final String findMaxTxAmountRestaurants = "select restaurant_id as restaurantId, restaurant_name as restaurantName, sum(transaction_amount) as txAmount"
            + " from purchase_history"
            + " group by restaurantId, restaurantName"
            + " having sum(transaction_amount) ="
            + " (select max(c.txSum) from (select restaurant_id, sum(transaction_amount) as txSum from purchase_history group by restaurant_id) c)";

    private static final String countTxAmountLessThan = COUNT_TX_AMOUNT
            + " where s.txSum < :amount";

    private static final String countTxAmountGreaterThan = COUNT_TX_AMOUNT
            + " where s.txSum > :amount";

    private DatabaseClient client;

    public PurchaseHistoryCustomRepositoryImpl(DatabaseClient client) {
        this.client = client;
    }

    public Flux<UserTxAmount> findTopTxUsers(int top, Instant fromDate, Instant toDate) {

        return client.execute(findTopTxUsers)
                .bind("top", top)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .map(mapUserTxAmount())
                .all();
    }

    public Flux<TxNumbAmount> findTxNumbAmount(Instant fromDate, Instant toDate) {

        return client.execute(findTxNumbAmount)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .map(mapTxNumbAmount())
                .all();
    }

    public Flux<RestaurantTxAmount> findMaxTxNumbRestaurants() {

        return client.execute(findMaxTxNumbRestaurants)
                .map(mapRestaurantTxAmount())
                .all();
    }

    public Flux<RestaurantTxAmount> findMaxTxAmountRestaurants() {

        return client.execute(findMaxTxAmountRestaurants)
                .map(mapRestaurantTxAmount())
                .all();
    }

    public Mono<Count> countTxAmountLessThan(BigDecimal amount, Instant fromDate, Instant toDate) {

        return client.execute(countTxAmountLessThan)
                .bind("amount", amount)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .map(mapCount())
                .one();
    }

    public Mono<Count> countTxAmountGreaterThan(BigDecimal amount, Instant fromDate, Instant toDate) {

        return client.execute(countTxAmountGreaterThan)
                .bind("amount", amount)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .map(mapCount())
                .one();
    }

    protected Function<Row, UserTxAmount> mapUserTxAmount() {

        return row -> UserTxAmount.builder()
                .userId(row.get("userId", Long.class))
                .userName(row.get("userName", String.class))
                .txAmount(row.get("txAmount", BigDecimal.class))
                .build();
    }

    protected Function<Row, RestaurantTxAmount> mapRestaurantTxAmount() {

        return row -> RestaurantTxAmount.builder()
                .restaurantId(row.get("restaurantId", Long.class))
                .restaurantName(row.get("restaurantName", String.class))
                .txAmount(row.get("txAmount", BigDecimal.class))
                .build();
    }

    protected Function<Row, RestaurantTxAmount> mapRestaurantTxNumb() {

        return row -> RestaurantTxAmount.builder()
                .restaurantId(row.get("restaurantId", Long.class))
                .restaurantName(row.get("restaurantName", String.class))
                .txNumb(row.get("txNumb", Long.class))
                .build();
    }

    protected Function<Row, TxNumbAmount> mapTxNumbAmount() {

        return row -> TxNumbAmount.builder()
                .txNumb(row.get("txNumb", Long.class))
                .txAmount(row.get("txAmount", BigDecimal.class))
                .build();
    }

    protected Function<Row, Count> mapCount() {

        return row -> Count.builder()
                .numb(row.get("numb", Long.class))
                .build();
    }
}
