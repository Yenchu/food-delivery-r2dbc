package idv.fd.etl;

import idv.fd.restaurant.model.OpenHours;
import idv.fd.restaurant.model.Restaurant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.BaseStream;

@Slf4j
@Component
public class OpenHoursDataParser {

    public static final String SUN = "Sun";
    public static final String MON = "Mon";
    public static final String TUE = "Tue";
    public static final String WED = "Wed";
    public static final String THU = "Thu";
    public static final String FRI = "Fri";
    public static final String SAT = "Sat";

    // To support both cases: Sun-? and ?-Sun
    public static final List<String> DAYS_OF_WEEK = Arrays.asList(SUN, MON, TUE, WED, THU, FRI, SAT, SUN);

    // parse restaurant name and open hours
    private String regex4RestaurantNameAndOpenHours = "\"(.*)\",\"(.*)\"";
    private Pattern pattern4RestaurantNameAndOpenHours = Pattern.compile(regex4RestaurantNameAndOpenHours);

    // parse days of week and open hours
    private String regex4OpenHours = "(.*?)(\\d{1,2}):?(\\d{2})?([a|p]m)-(\\d{1,2}):?(\\d{2})?([a|p]m)";
    private Pattern pattern4OpenHours = Pattern.compile(regex4OpenHours);

    public Flux<Tuple2<Restaurant, List<OpenHours>>> parseFile(String filePath) {

        return Flux.using(() -> Files.lines(Path.of(filePath)),
                Flux::fromStream,
                BaseStream::close
        ).map(this::parseLine).filter(t -> t != null);
    }

    public Tuple2<Restaurant, List<OpenHours>> parseLine(String line) {

        Matcher matcher = pattern4RestaurantNameAndOpenHours.matcher(line);
        if (matcher.find()) {
            String restaurantName = matcher.group(1);
            String openHoursStr = matcher.group(2);

            List<OpenHours> openHours = parseOpenHours(openHoursStr);

            Restaurant restaurant = new Restaurant();
            restaurant.setName(restaurantName);
            return Tuples.of(restaurant, openHours);
        } else {
            log.warn("unrecognised line format: {}", line);
            return null;
        }
    }

    public List<OpenHours> parseOpenHours(String ohs) {

        List<OpenHours> openHours = new ArrayList<>();

        ohs = ohs.replaceAll(" ", "");
        String[] ohArr = ohs.split("/");

        for (String oh : ohArr) {

            Matcher matcher = pattern4OpenHours.matcher(oh);
            if (matcher.find()) {
                String day = matcher.group(1);
                String openHrStr = matcher.group(2);
                String openMinStr = matcher.group(3);
                String openAmPm = matcher.group(4);
                String closedHrStr = matcher.group(5);
                String closedMinStr = matcher.group(6);
                String closedAmPm = matcher.group(7);

                // open time may be overnight, eg: 5:30 am - 3:30 am
                LocalTime overnight = null;

                int openHr = Integer.parseInt(openHrStr);
                int openMin = openMinStr != null ? Integer.parseInt(openMinStr) : 0;

                if (openAmPm.equalsIgnoreCase("pm") && openHr < 12) {

                    // convert 12 hour format to 24 hour format
                    openHr += 12;

                }

                int closedHr = Integer.parseInt(closedHrStr);
                int closedMin = closedMinStr != null ? Integer.parseInt(closedMinStr) : 0;

                if (closedAmPm.equalsIgnoreCase("pm") && closedHr < 12) {

                    // convert 12 hour format to 24 hour format
                    closedHr += 12;

                } else if (closedAmPm.equalsIgnoreCase("am")) {

                    // convert overnight open hours to TWO open hour periods
                    // from open time to midnight and from midnight to closed time next day
                    if (openAmPm.equalsIgnoreCase("pm") || openHr > closedHr) {

                        overnight = LocalTime.of(closedHr, closedMin);

                    }
                }

                LocalTime openTime = LocalTime.of(openHr, openMin);

                LocalTime closedTime;
                if (overnight != null) {
                    closedTime = LocalTime.MAX;
                } else {
                    closedTime = LocalTime.of(closedHr, closedMin);
                }

                parseDays(openHours, day, openTime, closedTime, overnight);
            } else {
                log.warn("unrecognised opening hours format: {}", oh);
            }
        }
        return openHours;
    }

    protected void parseDays(List<OpenHours> openHours, String days, LocalTime openTime, LocalTime closedTime, LocalTime overnight) {

        if (days.contains("-")) {

            String[] fromToDays = days.split("-");

            // check cases like `Mon, Weds - Thurs`
            int fromIdx;
            if (fromToDays[0].contains(",")) {

                String[] dayArr = fromToDays[0].split(",");
                createOpenHours(openHours, dayArr, openTime, closedTime, overnight);

                fromIdx = getDayOfWeekIdx(dayArr[dayArr.length - 1]) + 1;
            } else {
                fromIdx = getDayOfWeekIdx(fromToDays[0]);
            }

            // check cases like `Weds - Thurs, Sun`
            int toIdx;
            if (fromToDays[1].contains(",")) {

                String[] dayArr = fromToDays[1].split(",");
                createOpenHours(openHours, dayArr, openTime, closedTime, overnight);

                toIdx = getDayOfWeekIdxRevised(dayArr[0]) - 1;
            } else {
                toIdx = getDayOfWeekIdxRevised(fromToDays[1]);
            }

            for (int i = fromIdx; i <= toIdx; i++) {

                int dayIdx = i;

                // check cases like `Sat - Sun`
                // if toIdx is the last element(Sun), set it as first element(Sun)
                if (dayIdx == DAYS_OF_WEEK.size() - 1) {
                    dayIdx = 0;
                }

                OpenHours oh = createOpenHours(dayIdx, openTime, closedTime);
                openHours.add(oh);

                if (overnight != null) {
                    OpenHours overnightOpenHours = createOvernightOpenHours(oh.getDayOfWeek(), overnight);
                    openHours.add(overnightOpenHours);
                }
            }

        } else if (days.contains(",")) {

            String[] dayArr = days.split(",");
            createOpenHours(openHours, dayArr, openTime, closedTime, overnight);

        } else {

            OpenHours oh = createOpenHours(days, openTime, closedTime);
            openHours.add(oh);

            if (overnight != null) {
                OpenHours overnightOpenHours = createOvernightOpenHours(oh.getDayOfWeek(), overnight);
                openHours.add(overnightOpenHours);
            }

        }
    }

    protected void createOpenHours(List<OpenHours> openHours, String[] days, LocalTime openTime, LocalTime closedTime, LocalTime overnight) {

        for (String day : days) {
            OpenHours oh = createOpenHours(day, openTime, closedTime);
            openHours.add(oh);

            if (overnight != null) {
                OpenHours overnightOpenHours = createOvernightOpenHours(oh.getDayOfWeek(), overnight);
                openHours.add(overnightOpenHours);
            }
        }
    }

    protected OpenHours createOpenHours(String dayStr, LocalTime openTime, LocalTime closedTime) {

        if (dayStr.length() > 3) {
            dayStr = dayStr.substring(0, 3);
        }

        int day = DAYS_OF_WEEK.indexOf(dayStr);

        return createOpenHours(day, openTime, closedTime);
    }

    protected OpenHours createOpenHours(int day, LocalTime openTime, LocalTime closedTime) {

        long dur = Duration.between(openTime, closedTime).getSeconds();
        if (closedTime.equals(LocalTime.MAX)) {
            dur += 1;
        }
        int openPeriod = (int) dur / 60;

        OpenHours oh = OpenHours.builder()
                .dayOfWeek(day)
                .openPeriod(openPeriod)
                .build();

        oh.setOpenTime(openTime);
        oh.setClosedTime(closedTime);
        return oh;
    }

    protected OpenHours createOvernightOpenHours(int day, LocalTime overnight) {

        day++;
        if (day == 7) {
            day = 0;
        }

        return createOpenHours(day, LocalTime.MIDNIGHT, overnight);
    }

    private int getDayOfWeekIdx(String day) {

        if (day.length() > 3) {
            day = day.substring(0, 3);
        }

        // for cases: Sun - Mon
        return DAYS_OF_WEEK.indexOf(day);
    }

    private int getDayOfWeekIdxRevised(String day) {

        if (day.length() > 3) {
            day = day.substring(0, 3);
        }

        // for cases: Sat - Sun
        return DAYS_OF_WEEK.lastIndexOf(day);
    }
}
