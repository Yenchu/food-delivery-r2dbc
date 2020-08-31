package idv.fd.etl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantVo {

    private String restaurantName;

    private double cashBalance;

    private String openingHours;

    private List<MenuVo> menu;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MenuVo {

        private String dishName;

        private double price;

    }
}
