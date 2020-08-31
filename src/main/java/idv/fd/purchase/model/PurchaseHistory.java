package idv.fd.purchase.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseHistory {

    public static final String TX_DATE_FORMAT = "MM/dd/yyyy hh:mm a";

    public static final DateTimeFormatter TX_DATE_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern(TX_DATE_FORMAT)
            .toFormatter(Locale.ENGLISH); // Locale.ENGLISH is for parsing `AM/PM` as English

    @Id
    private Long id;

    private Long userId;

    private Long restaurantId;

    private String restaurantName;

    private Long menuId;

    private String dishName;

    private BigDecimal transactionAmount;

    @JsonFormat(pattern = TX_DATE_FORMAT, timezone = "UTC")
    private Instant transactionDate;

    public static Instant parseTxDate(String date) {

        return LocalDateTime.parse(date, TX_DATE_FORMATTER).atZone(ZoneId.of("UTC")).toInstant();
    }

}
