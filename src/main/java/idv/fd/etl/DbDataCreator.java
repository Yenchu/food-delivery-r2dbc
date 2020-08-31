package idv.fd.etl;

import idv.fd.etl.dto.RestaurantMenus;
import idv.fd.purchase.model.PurchaseHistory;
import idv.fd.restaurant.repository.MenuRepository;
import idv.fd.restaurant.repository.RestaurantRepository;
import idv.fd.restaurant.model.Menu;
import idv.fd.restaurant.model.OpenHours;
import idv.fd.restaurant.model.Restaurant;
import idv.fd.user.repository.UserRepository;
import idv.fd.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DbDataCreator {

    private RawDataExtractor rawDataExtractor;

    private RawDataTransformer rawDataTransformer;

    private DbDataLoader dbDataLoader;

    private RestaurantRepository restaurantRepository;

    private MenuRepository menuRepository;

    private UserRepository userRepository;

    public DbDataCreator(RawDataExtractor rawDataExtractor, OpenHoursDataParser openHoursDataParser, RawDataTransformer rawDataTransformer, DbDataLoader dbDataLoader, RestaurantRepository restaurantRepository, MenuRepository menuRepository, UserRepository userRepository) {
        this.rawDataExtractor = rawDataExtractor;
        this.rawDataTransformer = rawDataTransformer;
        this.dbDataLoader = dbDataLoader;
        this.restaurantRepository = restaurantRepository;
        this.menuRepository = menuRepository;
        this.userRepository = userRepository;
    }

    public Flux<Tuple3<Restaurant, List<Menu>, List<OpenHours>>> createRestaurantData() {

        if (restaurantRepository.findAll().collectList().block().size() > 0) {
            throw new RuntimeException("there are still restaurant data in database, please clean it first!");
        }

        return rawDataExtractor.extractRestaurantData()
                .map(rawDataTransformer::transformRestaurantData)
                .flatMap(dbDataLoader::loadRestaurantData);
    }

    public Flux<Tuple2<User, List<PurchaseHistory>>> createUserData() {

        if (userRepository.findAll().collectList().block().size() > 0) {
            throw new RuntimeException("there are still user data in database, please clean it first!");
        }

        Map<String, RestaurantMenus> restMenusMap = getRestaurantMenus();

        return rawDataExtractor.extractUserData()
                .map(rawDataTransformer::transformUserData)
                .flatMap(t -> dbDataLoader.loadUserData(t, restMenusMap));
    }

    protected Map<String, RestaurantMenus> getRestaurantMenus() {

        List<Restaurant> rs = restaurantRepository.findAll().collectList().block();

        Map<Long, RestaurantMenus> restMenusMap = rs.stream()
                .map(this::toRestaurantMenus)
                .collect(Collectors.toMap(RestaurantMenus::getId, Function.identity()));

        List<Menu> menus = menuRepository.findAll().collectList().block();

        for (Menu menu : menus) {

            Long restaurantId = menu.getRestaurantId();

            RestaurantMenus restMenus = restMenusMap.get(restaurantId);
            if (restMenus == null) {
                log.error("cannot find restaurant by id for menu: {}", menu);
                continue;
            }

            Map<String, Long> menusMap = restMenus.getMenus();

            if (menusMap.containsKey(menu.getDishName())) {
                log.error("dish name duplicated: {}", menu);
            }

            menusMap.put(menu.getDishName(), menu.getId());
        }

        return restMenusMap.values().stream().collect(Collectors.toMap(RestaurantMenus::getName, Function.identity()));
    }

    private RestaurantMenus toRestaurantMenus(Restaurant restaurant) {

        Map<String, Long> menusMap = new HashMap<>();

        return RestaurantMenus.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .menus(menusMap)
                .build();
    }
}
