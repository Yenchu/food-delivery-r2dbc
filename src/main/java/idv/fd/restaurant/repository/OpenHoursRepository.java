package idv.fd.restaurant.repository;

import idv.fd.restaurant.model.OpenHours;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface OpenHoursRepository extends ReactiveSortingRepository<OpenHours, Long>, OpenHoursCustomRepository {

    Flux<OpenHours> findAllByOrderByRestaurantId(Pageable pageable);

    Flux<OpenHours> findByRestaurantId(Long restaurantId);

    Flux<OpenHours> findByOpenTimeLessThanEqualAndClosedTimeGreaterThan(int openTime, int closedTime);

    Flux<OpenHours> findByDayOfWeekAndOpenTimeLessThanEqualAndClosedTimeGreaterThan(int dayOfWeek, int openTime, int closedTime);

}
