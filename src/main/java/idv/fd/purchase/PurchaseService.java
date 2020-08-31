package idv.fd.purchase;

import idv.fd.error.AppException;
import idv.fd.purchase.dto.*;
import idv.fd.purchase.model.PurchaseHistory;
import idv.fd.purchase.repository.PurchaseHistoryRepository;
import idv.fd.restaurant.MenuService;
import idv.fd.restaurant.RestaurantService;
import idv.fd.restaurant.model.Menu;
import idv.fd.restaurant.model.Restaurant;
import idv.fd.user.UserService;
import idv.fd.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@Slf4j
public class PurchaseService {

    private PurchaseHistoryRepository purchaseHistoryRepository;

    private UserService userService;

    private RestaurantService restaurantService;

    private MenuService menuService;

    public PurchaseService(PurchaseHistoryRepository purchaseHistoryRepository, UserService userService, RestaurantService restaurantService, MenuService menuService) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.userService = userService;
        this.restaurantService = restaurantService;
        this.menuService = menuService;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Mono<PurchaseHistory> purchaseDish(Purchase purchase) {

        return Mono.zip(
                userService.findUserByIdLocked(purchase.getUserId()),
                menuService.findMenuById(purchase.getMenuId())
        )
                .zipWhen(t2 -> restaurantService.findRestaurantByIdLocked(t2.getT2().getRestaurantId()))
                .flatMap(t3 -> {
                    User user = t3.getT1().getT1();
                    Menu menu = t3.getT1().getT2();
                    Restaurant rest = t3.getT2();

                    BigDecimal amount = menu.getPrice();

                    BigDecimal userNewBalance = user.getCashBalance().subtract(amount);

                    if (userNewBalance.longValue() <= 0) {
                        throw AppException.badRequest(String.format("user doesn't have enough balance to buy dish with price %d", amount));
                    }

                    user.setCashBalance(userNewBalance);

                    BigDecimal resNewBalance = rest.getCashBalance().add(amount);
                    rest.setCashBalance(resNewBalance);

                    PurchaseHistory ph = PurchaseHistory.builder()
                            .userId(user.getId())
                            .restaurantId(rest.getId())
                            .menuId(menu.getId())
                            .transactionAmount(amount)
                            .transactionDate(Instant.now())
                            .build();

                    return Mono.zip(userService.saveUser(user),
                            restaurantService.saveRestaurant(rest),
                            purchaseHistoryRepository.save(ph));
                }).map(t -> t.getT3())
                .doOnNext(p -> log.info("purchase record: {}", p));
    }

    public Flux<PurchaseHistory> findByUserId(Long userId) {

        return purchaseHistoryRepository.findByUserId(userId);
    }

    public Flux<PurchaseHistory> findByDateRange(Instant fromDate, Instant toDate) {

        return purchaseHistoryRepository.findByTransactionDateGreaterThanEqualAndTransactionDateLessThan(fromDate, toDate);
    }

    public Flux<UserTxAmount> findTopTxUsers(int top, Instant fromDate, Instant toDate) {

        return purchaseHistoryRepository.findTopTxUsers(top, fromDate, toDate);
    }

    public Flux<TxNumbAmount> findTxNumbAmount(Instant fromDate, Instant toDate) {

        return purchaseHistoryRepository.findTxNumbAmount(fromDate, toDate);
    }

    public Flux<RestaurantTxAmount> findMaxTxRestaurants(boolean byAmount) {

        if (byAmount) {
            return purchaseHistoryRepository.findMaxTxAmountRestaurants();
        } else {
            return purchaseHistoryRepository.findMaxTxNumbRestaurants();
        }
    }

    public Mono<Count> getUserCount(BigDecimal amount, Instant fromDate, Instant toDate, boolean lessThan) {

        if (lessThan) {
            return purchaseHistoryRepository.countTxAmountLessThan(amount, fromDate, toDate);
        } else {
            return purchaseHistoryRepository.countTxAmountGreaterThan(amount, fromDate, toDate);
        }
    }
}
