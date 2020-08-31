package idv.fd.restaurant;

import idv.fd.restaurant.model.OpenHours;
import idv.fd.restaurant.repository.OpenHoursRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalTime;

@Service
public class OpenHoursService {

    private OpenHoursRepository openHoursRepository;

    public OpenHoursService(OpenHoursRepository openHoursRepository) {
        this.openHoursRepository = openHoursRepository;
    }

    public Flux<OpenHours> findOpenHours(int page, int size) {

        PageRequest pr = PageRequest.of(page, size, Sort.by("restaurantId", "dayOfWeek"));
        return openHoursRepository.findAllByOrderByRestaurantId(pr);
    }

    public Flux<OpenHours> findOpenHoursByTime(LocalTime localTime) {

        int time = OpenHours.parseTime(localTime);
        return openHoursRepository.findByOpenTimeLessThanEqualAndClosedTimeGreaterThan(time, time);
    }

    public Flux<OpenHours> findOpenHoursByTime(int dayOfWeek, LocalTime localTime) {

        int time = OpenHours.parseTime(localTime);
        return openHoursRepository.findByDayOfWeekAndOpenTimeLessThanEqualAndClosedTimeGreaterThan(dayOfWeek, time, time);
    }

    public Flux<OpenHours> findOpenHoursByRestaurant(Long restaurantId) {

        return openHoursRepository.findByRestaurantId(restaurantId);
    }
}
