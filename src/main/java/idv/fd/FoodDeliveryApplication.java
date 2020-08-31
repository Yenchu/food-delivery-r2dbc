package idv.fd;

import idv.fd.etl.DbDataCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class FoodDeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodDeliveryApplication.class, args);
    }

    private static final int LOAD_RESTAURANT_DB_DATA = 1;
    private static final int LOAD_USER_DB_DATA = 2;

    @Value("${LOAD_DB_DATA}")
    private Integer loadDbData;

    @Bean
    public CommandLineRunner createDbData(ConfigurableApplicationContext ctx, DbDataCreator dbDataCreator) {

        return (args) -> {
            if (loadDbData == null || loadDbData == 0) {
                return;
            }

            if ((loadDbData & LOAD_RESTAURANT_DB_DATA) == LOAD_RESTAURANT_DB_DATA) {
                log.info("---------- create restaurant data...");
                long startTime = System.currentTimeMillis();

                dbDataCreator.createRestaurantData().blockLast();

                log.info("---------- create restaurant data ended: {}ms", (System.currentTimeMillis() - startTime));
            }

            if ((loadDbData & LOAD_USER_DB_DATA) == LOAD_USER_DB_DATA) {
                log.info("---------- create user data...");
                long startTime = System.currentTimeMillis();

                dbDataCreator.createUserData().blockLast();

                log.info("---------- create user data ended: {}ms", (System.currentTimeMillis() - startTime));
            }

            log.info("shutdown app...");
            int exitCode = SpringApplication.exit(ctx, () -> {
                // no errors
                return 0;
            });
            System.exit(exitCode);
        };
    }
}
