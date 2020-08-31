package idv.fd.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditRestaurant {

    private Long restaurantId;

    @NotBlank(message = "Restaurant name is mandatory")
    @Size(max = 128)
    private String restaurantName;

}
