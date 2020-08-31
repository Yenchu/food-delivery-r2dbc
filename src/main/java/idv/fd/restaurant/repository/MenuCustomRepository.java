package idv.fd.restaurant.repository;

import idv.fd.restaurant.dto.DishInfo;
import idv.fd.restaurant.dto.DishNumb;
import idv.fd.restaurant.model.Menu;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface MenuCustomRepository {

    Mono<Menu> findMenuByIdLocked(Long id);

    Flux<DishInfo> findByDishNameContaining(String dishName);

    Flux<DishInfo> findByPriceGreaterThanEqualAndPriceLessThanEqual(BigDecimal minPrice, BigDecimal maxPrice, String sortField);

    Flux<DishNumb> findByDishesLessThan(int dishNumb);

    Flux<DishNumb> findByDishesGreaterThan(int dishNumb);

    Flux<DishNumb> findByDishesLessThanAndWithinPrices(int dishNumb, BigDecimal minPrice, BigDecimal maxPrice);

    Flux<DishNumb> findByDishesGreaterThanAndWithinPrices(int dishNumb, BigDecimal minPrice, BigDecimal maxPrice);

}
