package idv.fd.restaurant;

import idv.fd.restaurant.api.OpenHoursApi;
import idv.fd.restaurant.model.OpenHours;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalTime;

@RestController
@Validated
@Slf4j
public class OpenHoursController implements OpenHoursApi {

    private OpenHoursService openHoursService;

    public OpenHoursController(OpenHoursService openHoursService) {
        this.openHoursService = openHoursService;
    }

    @GetMapping("/open-hours")
    public Flux<OpenHours> findOpenHours(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size) {

        return openHoursService.findOpenHours(page, size);
    }

    @GetMapping("/open-hours/findByTime")
    public Flux<OpenHours> findOpenHoursByTime(
            @RequestParam(name = "time") String timeStr,
            @RequestParam(name = "dayOfWeek", required = false) @Min(0) @Max(6) Integer dayOfWeek) {

        log.debug("find open hours by time {} dayOfWeek {}", timeStr, dayOfWeek);
        LocalTime time = OpenHours.parseLocalTime(timeStr);
        return openHoursService.findOpenHoursByTime(dayOfWeek, time);
    }

    @GetMapping("/open-hours/findByRestaurant")
    public Flux<OpenHours> findOpenHoursByRestaurant(
            @RequestParam(name = "restaurantId") Long restaurantId) {

        log.debug("find open hours by restaurant {}", restaurantId);
        return openHoursService.findOpenHoursByRestaurant(restaurantId);
    }
}
