package idv.fd.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favourite {

    @Id
    private Long id;

    private Long userId;

    private Long restaurantId;

    @Transient
    private String restaurantName;

}
