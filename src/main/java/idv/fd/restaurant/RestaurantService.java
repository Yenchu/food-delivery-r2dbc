package idv.fd.restaurant;

import idv.fd.restaurant.dto.EditRestaurant;
import idv.fd.restaurant.dto.QryDishNumb;
import idv.fd.restaurant.dto.QryOpenPeriod;
import idv.fd.restaurant.dto.RestaurantInfo;
import idv.fd.restaurant.model.OpenHours;
import idv.fd.restaurant.model.Restaurant;
import idv.fd.restaurant.repository.MenuRepository;
import idv.fd.restaurant.repository.OpenHoursRepository;
import idv.fd.restaurant.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalTime;

@Service
@Slf4j
public class RestaurantService {

    private RestaurantRepository restaurantRepository;

    private OpenHoursRepository openHoursRepository;

    private MenuRepository menuRepository;

    public RestaurantService(RestaurantRepository restaurantRepository, OpenHoursRepository openHoursRepository, MenuRepository menuRepository) {
        this.restaurantRepository = restaurantRepository;
        this.openHoursRepository = openHoursRepository;
        this.menuRepository = menuRepository;
    }

    public Flux<Restaurant> findRestaurants(int page, int size) {

        PageRequest pr = PageRequest.of(page, size, Sort.by("name"));
        return restaurantRepository.findAllByOrderByName(pr);
    }

    public Mono<Restaurant> findRestaurantById(Long id) {

        return restaurantRepository.findById(id);
    }

    @Transactional
    public Mono<Restaurant> findRestaurantByIdLocked(Long id) {

        return restaurantRepository.findRestaurantByIdLocked(id);
    }

    @Transactional
    public Mono<Restaurant> saveRestaurant(Restaurant restaurant) {

        return restaurantRepository.save(restaurant);
    }

    @Transactional
    public Mono<Restaurant> updateRestaurant(EditRestaurant editRest) {

        return findRestaurantByIdLocked(editRest.getRestaurantId()).flatMap(r -> {
            r.setName(editRest.getRestaurantName());
            return restaurantRepository.save(r);
        });
    }

    public Flux<RestaurantInfo> findRestaurantsByName(String name) {

        return restaurantRepository.findByNameContaining(name);
    }

    public Flux<RestaurantInfo> findRestaurantsByTime(LocalTime localTime) {

        int time = OpenHours.parseTime(localTime);
        return openHoursRepository.findRestaurantsByTime(time);
    }

    public Flux<RestaurantInfo> findRestaurantsByTime(int dayOfWeek, LocalTime localTime) {

        int time = OpenHours.parseTime(localTime);
        return openHoursRepository.findRestaurantsByDayAndTime(dayOfWeek, time);
    }

    public Flux<? extends RestaurantInfo> findRestaurantsByOpenPeriod(QryOpenPeriod qryOpenPeriod) {

        // open period in database is minute unit
        int openMinutes = qryOpenPeriod.getOpenHours() * 60;

        Flux<? extends RestaurantInfo> rests;

        if (qryOpenPeriod.isPerWeek()) {

            if (qryOpenPeriod.isLessThan()) {
                rests = openHoursRepository.findWeekOpenPeriodLessThan(openMinutes);
            } else {
                rests = openHoursRepository.findWeekOpenPeriodGreaterThan(openMinutes);
            }
        } else {

            if (qryOpenPeriod.isLessThan()) {
                rests = openHoursRepository.findOpenPeriodLessThan(openMinutes);
            } else {
                rests = openHoursRepository.findOpenPeriodGreaterThan(openMinutes);
            }
        }
        return rests;
    }

    public Flux<? extends RestaurantInfo> findRestaurantsByDishNumb(QryDishNumb qryDishNumb) {

        int dishNumb = qryDishNumb.getDishNumb();

        Flux<? extends RestaurantInfo> rests;

        BigDecimal maxPrice = qryDishNumb.getMaxPrice();

        if (maxPrice != null && maxPrice.doubleValue() > 0) {

            BigDecimal minPrice = qryDishNumb.getMinPrice() != null ? qryDishNumb.getMinPrice() : BigDecimal.ZERO;

            if (qryDishNumb.isLessThan()) {
                rests = menuRepository.findByDishesLessThanAndWithinPrices(dishNumb, minPrice, maxPrice);
            } else {
                rests = menuRepository.findByDishesGreaterThanAndWithinPrices(dishNumb, minPrice, maxPrice);
            }
        } else {

            if (qryDishNumb.isLessThan()) {
                rests = menuRepository.findByDishesLessThan(dishNumb);
            } else {
                rests = menuRepository.findByDishesGreaterThan(dishNumb);
            }
        }
        return rests;
    }
}
