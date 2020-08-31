package idv.fd.restaurant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalTime;

@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenHours {

    public static final String MIDNIGHT = "00:00";

    @Id
    private Long id;

    private Long restaurantId;

    @Transient
    private String restaurantName;

    private int dayOfWeek;

    private int openTime;

    private int closedTime;

    private int openPeriod; // minutes

    @JsonIgnore
    public int getOpenTime() {

        return openTime;
    }

    @JsonIgnore
    public int getClosedTime() {

        return closedTime;
    }

    public String getOpenHour() {

        if (openTime == 0) {
            return MIDNIGHT;
        }
        return toTimeStr(openTime);
    }

    public String getClosedHour() {

        return toTimeStr(closedTime);
    }

    public void setOpenTime(LocalTime time) {

        this.openTime = parseTime(time);
    }

    public void setClosedTime(LocalTime time) {

        this.closedTime = parseTime(time);
    }

    public static String toTimeStr(int time) {

        StringBuilder s = new StringBuilder();
        s.append(time).insert(s.length() - 2, ':');
        if (s.length() < 5) {
            s.insert(0, '0');
        }
        return s.toString();
    }

    public static int toTimeInt(String time) {

        String[] hm = time.split(":");
        if (hm.length != 2) {
            throw new IllegalArgumentException("time format should be HH:mm but got " + time);
        }

        int h = Integer.parseInt(hm[0]);
        int m = Integer.parseInt(hm[1]);
        return h * 100 + m;
    }

    public static int parseTime(LocalTime time) {

        return time.getHour() * 100 + time.getMinute();
    }

    public static LocalTime parseLocalTime(String time) {

        if (time.indexOf(":") == 1) {
            time = "0" + time;
        }

        // format is `HH:mm`, no AM/PM
        return LocalTime.parse(time);
    }
}
