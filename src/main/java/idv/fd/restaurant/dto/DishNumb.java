package idv.fd.restaurant.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public class DishNumb extends RestaurantInfo {

    private Long dishNumb;

}
