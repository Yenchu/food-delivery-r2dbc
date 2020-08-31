package idv.fd.restaurant.api;

import idv.fd.error.AppError;
import idv.fd.restaurant.dto.DishInfo;
import idv.fd.restaurant.dto.EditMenu;
import idv.fd.restaurant.model.Menu;
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
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Tag(name = "Menu", description = "The Menu APIs")
public interface MenuApi {

    @Operation(summary = "Edit dish name, dish price of menu", tags = {"Menu"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = Menu.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @PutMapping("/menus")
    Mono<Menu> updateMenu(@Valid @RequestBody EditMenu editMenu);


    @Operation(summary = "Flux all dishes that are within a price range, sorted by price or alphabetically", tags = {"Menu"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Menu.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping("/menus/findByPrice")
    Flux<DishInfo> findMenusWithinPrices(
            @Parameter(name = "maxPrice", description = "Max dish price (min = 1.0)") @Min(1) BigDecimal maxPrice,
            @Parameter(name = "minPrice", description = "Min dish price") BigDecimal minPrice,
            @Parameter(name = "sortByPrice", description = "Sorted by price or dish name") boolean sortByPrice);


    @Operation(summary = "Search dishes by dish name", tags = {"Menu"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Menu.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping("/menus/findByDishName")
    Flux<DishInfo> findMenusByDishName(
            @Parameter(name = "dishName", description = "The specified dish name") String dishName);

}
