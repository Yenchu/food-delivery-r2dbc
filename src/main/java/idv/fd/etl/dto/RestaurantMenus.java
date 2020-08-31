package idv.fd.etl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantMenus {

    private Long id;

    private String name;

    private Map<String, Long> menus;

}
