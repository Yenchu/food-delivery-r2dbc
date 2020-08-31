package idv.fd.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QryOpenPeriod {

    private int openHours;

    private boolean lessThan;

    private boolean perWeek;

}
