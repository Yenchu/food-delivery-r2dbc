package idv.fd.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QryDishNumb {

    private int dishNumb;

    private boolean lessThan;

    private BigDecimal maxPrice;

    private BigDecimal minPrice;

}
