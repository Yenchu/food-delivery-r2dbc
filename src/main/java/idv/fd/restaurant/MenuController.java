package idv.fd.restaurant;

import idv.fd.restaurant.api.MenuApi;
import idv.fd.restaurant.dto.DishInfo;
import idv.fd.restaurant.dto.EditMenu;
import idv.fd.restaurant.model.Menu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@RestController
@Validated
@Slf4j
public class MenuController implements MenuApi {

    private MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * Edit restaurant name, dish name, dish price and user name
     *
     * @param editMenu
     * @return
     */
    @PutMapping("/menus")
    public Mono<Menu> updateMenu(@Valid @RequestBody EditMenu editMenu) {

        log.info("update menu: {}", editMenu);
        return menuService.updateMenu(editMenu);
    }

    /**
     * 4. Flux all dishes that are within a price range, sorted by price or alphabetically
     *
     * @param maxPrice
     * @param minPrice
     * @param sortByPrice
     * @return
     */
    @GetMapping("/menus/findByPrice")
    public Flux<DishInfo> findMenusWithinPrices(
            @RequestParam(name = "maxPrice") @Min(1) BigDecimal maxPrice,
            @RequestParam(name = "minPrice", required = false, defaultValue = "0") BigDecimal minPrice,
            @RequestParam(name = "sortByPrice", required = false, defaultValue = "true") boolean sortByPrice) {

        log.debug("find menus within maxPrice {} minPrice {} sortByPrice {}", maxPrice, minPrice, sortByPrice);
        return menuService.findMenusWithinPrices(minPrice, maxPrice, sortByPrice);
    }

    /**
     * 7. Search for restaurants or dishes by name, ranked by relevance to search term
     *
     * @param dishName
     * @return
     */
    @GetMapping("/menus/findByDishName")
    public Flux<DishInfo> findMenusByDishName(String dishName) {

        log.debug("find menus by dish name {}", dishName);
        return menuService.findMenusByDishName(dishName);
    }
}
