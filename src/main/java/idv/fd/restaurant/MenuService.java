package idv.fd.restaurant;

import idv.fd.restaurant.dto.DishInfo;
import idv.fd.restaurant.dto.EditMenu;
import idv.fd.restaurant.model.Menu;
import idv.fd.restaurant.repository.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public class MenuService {

    private MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public Mono<Menu> findMenuById(Long id) {

        return menuRepository.findById(id);
    }

    @Transactional
    public Mono<Menu> findMenuByIdLocked(Long id) {

        return menuRepository.findMenuByIdLocked(id);
    }

    @Transactional
    public Mono<Menu> updateMenu(EditMenu editMenu) {

        return findMenuByIdLocked(editMenu.getMenuId()).flatMap(m -> {
            m.setDishName(editMenu.getDishName());
            m.setPrice(editMenu.getPrice());
            return menuRepository.save(m);
        });
    }

    public Flux<DishInfo> findMenusByDishName(String dishName) {

        return menuRepository.findByDishNameContaining(dishName);
    }

    public Flux<DishInfo> findMenusWithinPrices(BigDecimal minPrice, BigDecimal maxPrice) {

        return findMenusWithinPrices(minPrice, maxPrice, true);
    }

    public Flux<DishInfo> findMenusWithinPrices(BigDecimal minPrice, BigDecimal maxPrice, boolean sortByPrice) {

        // use native sql to sort, so fields need to be database column name
        String sortField = sortByPrice ? "dish_name" : "price";

        return menuRepository.findByPriceGreaterThanEqualAndPriceLessThanEqual(minPrice, maxPrice, sortField);
    }
}
