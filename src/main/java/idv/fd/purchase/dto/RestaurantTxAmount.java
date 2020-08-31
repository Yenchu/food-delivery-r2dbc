package idv.fd.purchase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantTxAmount {

    private Long restaurantId;

    private String restaurantName;

    private Long txNumb;

    private BigDecimal txAmount;

}
