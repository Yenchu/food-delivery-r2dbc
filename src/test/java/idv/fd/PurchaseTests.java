package idv.fd;

import com.fasterxml.jackson.databind.ObjectMapper;
import idv.fd.purchase.PurchaseService;
import idv.fd.purchase.dto.Purchase;
import idv.fd.purchase.model.PurchaseHistory;
import idv.fd.restaurant.MenuService;
import idv.fd.restaurant.RestaurantService;
import idv.fd.restaurant.model.Menu;
import idv.fd.restaurant.model.Restaurant;
import idv.fd.user.UserService;
import idv.fd.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PurchaseTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private MenuService menuService;

    @Test
    public void findTopTxUsers() {

        int top = 10;
        Instant fromDate = Instant.parse("2018-12-01T11:00:00.00Z");
        Instant toDate = Instant.parse("2019-12-01T22:00:00.00Z");

        purchaseService.findTopTxUsers(top, fromDate, toDate)
                .doOnNext(u -> System.out.println(TestUtil.toJson(objectMapper, u)))
                .collectList()
                .as(StepVerifier::create)
                .assertNext(l -> assertThat(l.size()).isLessThanOrEqualTo(top))
                .verifyComplete();
    }

    @Test
    @Transactional
    public void purchaseDish() {

        Long userId = 1L;
        User user = userService.findUserById(userId).block();
        BigDecimal prevUserBalance = user.getCashBalance();

        Long menuId = 1L;
        Menu menu = menuService.findMenuById(menuId).block();
        BigDecimal dishPrice = menu.getPrice();

        Long restaurantId = menu.getRestaurantId();
        Restaurant rest = restaurantService.findRestaurantById(restaurantId).block();
        BigDecimal prevRestBalance = rest.getCashBalance();
        System.out.println(String.format("before userBalance=%f, restaurantBalance=%f dishPrice=%f",
                prevUserBalance.doubleValue(), prevRestBalance.doubleValue(), dishPrice.doubleValue()));

        Purchase p = Purchase.builder()
                .userId(userId)
                .menuId(menuId)
                .build();

        PurchaseHistory ph = purchaseService.purchaseDish(p).block();

        user = userService.findUserById(userId).block();
        rest = restaurantService.findRestaurantById(restaurantId).block();
        System.out.println(String.format("after userBalance=%f, restaurantBalance=%f",
                user.getCashBalance().doubleValue(), rest.getCashBalance().doubleValue()));

        assertThat(user.getCashBalance()).isEqualTo(prevUserBalance.subtract(dishPrice));
        assertThat(rest.getCashBalance()).isEqualTo(prevRestBalance.add(dishPrice));
    }

    @Test
    public void findByUserId() {

        Long userId = 1L;
        purchaseService.findByUserId(userId)
                .as(StepVerifier::create)
                .assertNext(ph -> assertThat(ph.getUserId()).isEqualTo(userId))
                .verifyComplete();
    }

    @Test
    public void findByDateRange() {

        LocalDateTime fromL = LocalDateTime.of(2020, 8, 27, 14, 15, 59);
        Instant from = fromL.atZone(ZoneId.of("UTC")).toInstant();
        LocalDateTime toL = LocalDateTime.of(2020, 8, 27, 15, 0, 0);
        Instant to = toL.atZone(ZoneId.of("UTC")).toInstant();
        System.out.println("from " + from + " to " + to);

        purchaseService.findByDateRange(from, to)
                .doOnNext(System.out::println)
                .collectList()
                .as(StepVerifier::create)
                .assertNext(phs ->
                        assertThat(phs).allMatch(ph -> ph.getTransactionDate().compareTo(from) >= 0)
                                .allMatch(ph -> ph.getTransactionDate().compareTo(to) < 0)
                )
                .verifyComplete();
    }
}
