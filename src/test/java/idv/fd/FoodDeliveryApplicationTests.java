package idv.fd;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import idv.fd.etl.*;
import idv.fd.etl.dto.RestaurantVo;
import idv.fd.etl.dto.UserVo;
import idv.fd.restaurant.model.OpenHours;
import idv.fd.restaurant.model.Restaurant;
import idv.fd.user.repository.FavouriteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootTest
class FoodDeliveryApplicationTests {

    @Autowired
    private FavouriteRepository favouriteRepository;

    @Autowired
    private OpenHoursDataParser openHoursDataParser;

    @Autowired
    private RawDataExtractor rawDataExtractor;

    @Autowired
    private RawDataTransformer rawDataTransformer;

    @Autowired
    private DbDataLoader dbDataLoader;

    @Autowired
    private DbDataCreator dbDataCreator;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void load() throws IOException {

        String filePath = "./data/testData.json";

        TypeReference<List<RestaurantVo>> typeRef = new TypeReference<>() {
        };

        List<RestaurantVo> vos = objectMapper.readValue(new File(filePath), typeRef);

        Flux.fromIterable(vos).map(rawDataTransformer::transformRestaurantData)
                .map(dbDataLoader::loadRestaurantData)
                .doOnNext(System.out::println)
                .blockLast();
    }

    @Test
    public void extractRestaurantData() {

        RestaurantVo vo = rawDataExtractor.extractRestaurantData().blockLast();
        System.out.println("last restaurant data: " + vo);
    }

    @Test
    public void extractUserData() {

        UserVo vo = rawDataExtractor.extractUserData().blockLast();
        System.out.println("last user data: " + vo);
    }

    @Test
    void parseOpenHoursData() {

        String line = "\"Plumed Horse\",\"Mon 11:45 am - 9:15 pm / Tues 7:45 am - 12:30 pm / Weds - Thurs, Sun 7:45 am - 3:45 pm / Fri 7 am - 3:45 am / Sat 6 am - 3:30 pm\"";
        Tuple2<Restaurant, List<OpenHours>> tp = openHoursDataParser.parseLine(line);
        System.out.println(tp);

        line = "\"Sudachi\",\"Mon-Wed 5 pm - 12:30 am  / Thu-Fri 5 pm - 1:30 am  / Sat 3 pm - 1:30 am  / Sun 3 pm - 11:30 pm\"";
        tp = openHoursDataParser.parseLine(line);
        System.out.println(tp);
    }

    @Test
    public void findFavourites() {

        favouriteRepository.findAll().doOnNext(System.out::println).blockLast();
    }
}
