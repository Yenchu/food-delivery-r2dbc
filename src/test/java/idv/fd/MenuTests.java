package idv.fd;

import com.fasterxml.jackson.databind.ObjectMapper;
import idv.fd.restaurant.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
public class MenuTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MenuService menuService;

    @Test
    public void findMenusWithinPrices() {

        BigDecimal maxPrice = BigDecimal.valueOf(14.5);
        BigDecimal minPrice = BigDecimal.ZERO;

        menuService.findMenusWithinPrices(minPrice, maxPrice)
                //.take(10)
                //.doOnNext(oh -> System.out.println(TestUtil.toJson(objectMapper, oh)))
                .collectList()
                .as(StepVerifier::create)
                .assertNext(dishes ->
                        assertThat(dishes).allMatch(dish -> dish.getPrice().compareTo(maxPrice) <= 0)
                                .allMatch(dish -> dish.getPrice().compareTo(minPrice) >= 0)
                )
                .verifyComplete();
    }
}
