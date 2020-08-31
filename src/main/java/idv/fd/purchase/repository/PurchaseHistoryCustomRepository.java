package idv.fd.purchase.repository;

import idv.fd.purchase.dto.Count;
import idv.fd.purchase.dto.RestaurantTxAmount;
import idv.fd.purchase.dto.TxNumbAmount;
import idv.fd.purchase.dto.UserTxAmount;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;

public interface PurchaseHistoryCustomRepository {

    Flux<UserTxAmount> findTopTxUsers(int top, Instant fromDate, Instant toDate);

    Flux<TxNumbAmount> findTxNumbAmount(Instant fromDate, Instant toDate);

    Flux<RestaurantTxAmount> findMaxTxNumbRestaurants();

    Flux<RestaurantTxAmount> findMaxTxAmountRestaurants();

    Mono<Count> countTxAmountLessThan(BigDecimal amount, Instant fromDate, Instant toDate);

    Mono<Count> countTxAmountGreaterThan(BigDecimal amount, Instant fromDate, Instant toDate);

}
