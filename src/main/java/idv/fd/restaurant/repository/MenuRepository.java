package idv.fd.restaurant.repository;

import idv.fd.restaurant.model.Menu;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface MenuRepository extends ReactiveSortingRepository<Menu, Long>, MenuCustomRepository {

    //Flux<Menu> findByDishNameContainingOrderByDishName(String dishName);

    //Flux<Menu> findByPriceGreaterThanEqualAndPriceLessThanEqual(BigDecimal minPrice, BigDecimal maxPrice, Sort sort);

}
