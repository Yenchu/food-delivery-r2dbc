package idv.fd.restaurant.api;


import idv.fd.error.AppError;
import idv.fd.restaurant.dto.EditRestaurant;
import idv.fd.restaurant.dto.RestaurantInfo;
import idv.fd.restaurant.model.Restaurant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Tag(name = "Restaurant", description = "The Restaurant APIs")
public interface RestaurantApi {

    @Operation(summary = "Find restaurants by pagination", tags = {"Restaurant"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Restaurant.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping("/restaurants")
    Flux<Restaurant> findRestaurants(
            @Parameter(name = "page", description = "The specified page") int page,
            @Parameter(name = "size", description = "The number of records in the page") int size);


    @Operation(summary = "Edit restaurant name", tags = {"Restaurant"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = Restaurant.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @PutMapping("/restaurants")
    Mono<Restaurant> updateRestaurant(@Valid @RequestBody EditRestaurant editRest);


    @Operation(summary = "Flux all restaurants that are open at a certain time on a day of the week", tags = {"Restaurant"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantInfo.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping("/restaurants/findByTime")
    Flux<RestaurantInfo> findRestaurantsByTime(
            @Parameter(name = "time", description = "The specified open time (format is HH:mm), eg: 18:30") String timeStr,
            @Parameter(name = "dayOfWeek", description = "A day of week (Sun = 0, Sat = 6)") @Min(0) @Max(6) Integer dayOfWeek);


    @Operation(summary = "Flux all restaurants that are open for more or less than x hours per day or week", tags = {"Restaurant"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantInfo.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping("/restaurants/findByOpenHours")
    Flux<? extends RestaurantInfo> findRestaurantsByOpenPeriod(
            @Parameter(name = "openHours", description = "The specified open hours (min = 1, max = 24)") @Min(1) int openHours,
            @Parameter(name = "lessThan", description = "To indicate less or more than the specified open hours") boolean lessThan,
            @Parameter(name = "perWeek", description = "To indicate per week or per day") boolean perWeek);


    @Operation(summary = "Flux all restaurants that have more or less than x number of dishes within a price range", tags = {"Restaurant"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantInfo.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping("restaurants/findByDishNumb")
    Flux<? extends RestaurantInfo> findRestaurantsByDishNumb(
            @Parameter(name = "dishNumb", description = "The specified dish number (min = 1, max = 1000)") @Min(1) @Max(1000) int dishNumb,
            @Parameter(name = "lessThan", description = "To indicate less or more than the specified dish number") boolean lessThan,
            @Parameter(name = "maxPrice", description = "Max dish price") BigDecimal maxPrice,
            @Parameter(name = "minPrice", description = "Min dish price") BigDecimal minPrice);


    @Operation(summary = "Search restaurants by name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantInfo.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping("/restaurants/findByName")
    Flux<RestaurantInfo> findRestaurantsByName(
            @Parameter(name = "name", description = "The specified restaurant name") String name);

}
