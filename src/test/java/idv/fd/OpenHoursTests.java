package idv.fd;

import com.fasterxml.jackson.databind.ObjectMapper;
import idv.fd.restaurant.OpenHoursService;
import idv.fd.restaurant.model.OpenHours;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OpenHoursTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OpenHoursService openHoursService;

    @Test
    public void findOpenHours() {

        openHoursService.findOpenHours(0, 10)
                .take(10)
                .doOnNext(System.out::println)
                .blockLast();
    }

    @Test
    public void findOpenHoursByRestaurant() {

        Long restaurantId = 1L;

        openHoursService.findOpenHoursByRestaurant(restaurantId)
                .doOnNext(oh -> System.out.println(TestUtil.toJson(objectMapper, oh)))
                .collectList()
                .as(StepVerifier::create)
                .assertNext(ohs ->
                        assertThat(ohs).allMatch(oh -> oh.getRestaurantId().equals(restaurantId))
                )
                .verifyComplete();
    }

    @Test
    public void findOpenHoursByTime() {

        String timeStr = "6:00";
        final LocalTime time = OpenHours.parseLocalTime(timeStr);
        System.out.println("time: " + time.toString());

        openHoursService.findOpenHoursByTime(time)
                .take(10)
                .doOnNext(oh -> System.out.println(TestUtil.toJson(objectMapper, oh)))
                .collectList()
                .as(StepVerifier::create)
                .assertNext(ohs ->
                        assertThat(ohs).allMatch(oh -> oh.getOpenHour().compareTo(time.toString()) <= 0)
                                .allMatch(oh -> oh.getClosedHour().compareTo(time.toString()) > 0)
                )
                .verifyComplete();
    }

    @Test
    public void findOpenHoursByDayAndTime() {

        int dayOfWeek = 1;
        final LocalTime time = OpenHours.parseLocalTime("19:30");

        openHoursService.findOpenHoursByTime(dayOfWeek, time)
                .collectList()
                .as(StepVerifier::create)
                .assertNext(ohs ->
                        assertThat(ohs).allMatch(oh -> oh.getDayOfWeek() == dayOfWeek)
                                .allMatch(oh -> oh.getOpenHour().compareTo(time.toString()) <= 0)
                                .allMatch(oh -> oh.getClosedHour().compareTo(time.toString()) > 0)
                )
                .verifyComplete();
    }
}
