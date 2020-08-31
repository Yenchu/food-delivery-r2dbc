package idv.fd.restaurant.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public class DishInfo extends RestaurantInfo {

    private Long menuId;

    private String dishName;

    private BigDecimal price;

    //@PersistenceConstructor
//    public DishInfo(Long restaurantId, String restaurantName, Long menuId, String dishName, BigDecimal price) {
//        this.restaurantId = restaurantId;
//        this.restaurantName = restaurantName;
//        this.menuId = menuId;
//        this.dishName = dishName;
//        this.price = price;
//    }

}
