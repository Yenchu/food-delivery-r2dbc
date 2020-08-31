package idv.fd.purchase.repository;

import idv.fd.purchase.model.PurchaseHistory;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

import java.time.Instant;

public interface PurchaseHistoryRepository extends ReactiveSortingRepository<PurchaseHistory, Long>, PurchaseHistoryCustomRepository {

    Flux<PurchaseHistory> findByUserId(Long userId);

    Flux<PurchaseHistory> findByTransactionDateGreaterThanEqualAndTransactionDateLessThan(Instant fromDate, Instant toDate);

}
