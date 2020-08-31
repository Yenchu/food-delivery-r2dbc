package idv.fd.restaurant.repository;

import idv.fd.restaurant.dto.RestaurantInfo;
import idv.fd.restaurant.dto.WeekOpenPeriod;
import reactor.core.publisher.Flux;

public interface OpenHoursCustomRepository {

    Flux<RestaurantInfo> findRestaurantsByTime(int time);

    Flux<RestaurantInfo> findRestaurantsByDayAndTime(int dayOfWeek, int time);

    Flux<RestaurantInfo> findOpenPeriodLessThan(int minutes);

    Flux<RestaurantInfo> findOpenPeriodGreaterThan(int minutes);

    Flux<WeekOpenPeriod> findWeekOpenPeriodLessThan(int minutes);

    Flux<WeekOpenPeriod> findWeekOpenPeriodGreaterThan(int minutes);

}
