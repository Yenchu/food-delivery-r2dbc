package idv.fd.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditMenu {

    private Long menuId;

    @NotBlank(message = "Dish name is mandatory")
    @Size(max = 500)
    private String dishName;

    @DecimalMin(value = "0.0")
    @Digits(integer=6, fraction=2)
    private BigDecimal price;

}
