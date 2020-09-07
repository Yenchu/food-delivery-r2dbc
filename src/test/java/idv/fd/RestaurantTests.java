package idv.fd;

import com.fasterxml.jackson.databind.ObjectMapper;
import idv.fd.config.RestaurantTestService;
import idv.fd.config.TestConfig;
import idv.fd.restaurant.RestaurantService;
import idv.fd.restaurant.dto.EditRestaurant;
import idv.fd.restaurant.dto.QryOpenPeriod;
import idv.fd.restaurant.model.OpenHours;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestConfig.class)
@Slf4j
public class RestaurantTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private RestaurantTestService restaurantTestService;

    @Test
    public void updateRestaurant() {

        Long id = 2L;

        // 024 Grille
        restaurantService.findRestaurantById(id).doOnNext(r -> log.debug("restaurant: {}", r.getName())).block();

        Thread t = new Thread(() -> restaurantTestService.updateRestaurantLocked(EditRestaurant.builder()
                .restaurantId(id)
                .restaurantName("==Sleepy==")
                .build())
                //.subscribeOn(Schedulers.boundedElastic())
                .subscribe(r -> log.debug("update 1: {}", r.getName())));
        t.start();

        Thread t2 = new Thread(() -> restaurantTestService.updateRestaurant(EditRestaurant.builder()
                .restaurantId(id)
                .restaurantName("024 Grille")
                .build())
                //.subscribeOn(Schedulers.boundedElastic())
                .subscribe(r -> log.debug("update 2: {}", r.getName())));
        t2.start();

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("----------");
    }

    @Test
    public void findRestaurantsByName() {

        String name = "the";

        restaurantService.findRestaurantsByName(name)
                //.doOnNext(r -> System.out.println(TestUtil.toJson(objectMapper, r)))
                .collectList()
                .as(StepVerifier::create)
                .assertNext(rs ->
                        assertThat(rs).allMatch(r -> r.getRestaurantName().toLowerCase().contains(name))
                )
                .verifyComplete();
    }

    @Test
    public void findRestaurantsByTime() {

        LocalTime time = OpenHours.parseLocalTime("09:00");

        restaurantService.findRestaurantsByTime(time)
                .take(10)
                .map(r -> TestUtil.toJson(objectMapper, r))
                .doOnNext(System.out::println)
                .blockLast();
    }

    @Test
    public void findRestaurantsByOpenPeriod() {

        QryOpenPeriod qryOpenPeriod = QryOpenPeriod.builder().openHours(6).build();

        restaurantService.findRestaurantsByOpenPeriod(qryOpenPeriod)
                .take(10)
                .map(r -> TestUtil.toJson(objectMapper, r))
                .doOnNext(System.out::println)
                .blockLast();


        qryOpenPeriod.setPerWeek(true);
        qryOpenPeriod.setOpenHours(60);

        restaurantService.findRestaurantsByOpenPeriod(qryOpenPeriod)
                .take(10)
                .map(r -> TestUtil.toJson(objectMapper, r))
                .doOnNext(System.out::println)
                .blockLast();
    }

}
