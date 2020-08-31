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
public class UserVo {

    private Long id;

    private String name;

    private double cashBalance;

    private List<PurchaseHistoryVo> purchaseHistory;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PurchaseHistoryVo {

        private String restaurantName;

        private String dishName;

        private double transactionAmount;

        // 04/03/2020 01:56 PM
        private String transactionDate;

    }

}
