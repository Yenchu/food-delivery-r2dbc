package idv.fd.etl;

import idv.fd.etl.dto.RestaurantMenus;
import idv.fd.purchase.repository.PurchaseHistoryRepository;
import idv.fd.purchase.model.PurchaseHistory;
import idv.fd.restaurant.repository.MenuRepository;
import idv.fd.restaurant.repository.OpenHoursRepository;
import idv.fd.restaurant.repository.RestaurantRepository;
import idv.fd.restaurant.model.Menu;
import idv.fd.restaurant.model.OpenHours;
import idv.fd.restaurant.model.Restaurant;
import idv.fd.user.repository.UserRepository;
import idv.fd.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class DbDataLoader {

    private RestaurantRepository restaurantRepository;

    private MenuRepository menuRepository;

    private OpenHoursRepository openHoursRepository;

    private UserRepository userRepository;

    private PurchaseHistoryRepository purchaseHistoryRepository;

    public DbDataLoader(RestaurantRepository restaurantRepository, MenuRepository menuRepository, OpenHoursRepository openHoursRepository, UserRepository userRepository, PurchaseHistoryRepository purchaseHistoryRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuRepository = menuRepository;
        this.openHoursRepository = openHoursRepository;
        this.userRepository = userRepository;
        this.purchaseHistoryRepository = purchaseHistoryRepository;
    }

    @Transactional
    public Mono<Tuple3<Restaurant, List<Menu>, List<OpenHours>>> loadRestaurantData(Tuple3<Restaurant, List<Menu>, List<OpenHours>> tuple) {

        Restaurant restaurant = tuple.getT1();
        List<Menu> menus = tuple.getT2();
        List<OpenHours> ohs = tuple.getT3();

        return restaurantRepository.save(restaurant).zipWhen(r -> {

            Long restaurantId = r.getId();

            for (Menu m : menus) {
                m.setRestaurantId(restaurantId);
            }

            for (OpenHours oh : ohs) {
                oh.setRestaurantId(restaurantId);
            }
            return Mono.zip(menuRepository.saveAll(menus).collectList(), openHoursRepository.saveAll(ohs).collectList());

        }).map(t -> Tuples.of(t.getT1(), t.getT2().getT1(), t.getT2().getT2()));
    }

    @Transactional
    public Mono<Tuple2<User, List<PurchaseHistory>>> loadUserData(Tuple2<User, List<PurchaseHistory>> tuple, Map<String, RestaurantMenus> restMenusMap) {

        User user = tuple.getT1();
        List<PurchaseHistory> phs = tuple.getT2();

        return userRepository.save(user).zipWhen(u -> {

            updatePurchaseHistory(restMenusMap, user, phs);

            return purchaseHistoryRepository.saveAll(phs).collectList();
        });
    }

    protected void updatePurchaseHistory(Map<String, RestaurantMenus> restMenusMap, User user, List<PurchaseHistory> phs) {

        for (PurchaseHistory ph : phs) {

            ph.setUserId(user.getId());

            RestaurantMenus restMenus = restMenusMap.get(ph.getRestaurantName());
            if (restMenus == null) {
                log.error("can not find restaurant for purchase history: {}", ph);
                continue;
            }

            ph.setRestaurantId(restMenus.getId());

            Long menuId = restMenus.getMenus().get(ph.getDishName());
            if (menuId == null) {
                log.error("can not find menu for purchase history: {}", ph);
                continue;
            }

            ph.setMenuId(menuId);
        }
    }
}
