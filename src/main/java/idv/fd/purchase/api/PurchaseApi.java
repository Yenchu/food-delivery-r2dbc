package idv.fd.purchase.api;

import idv.fd.error.AppError;
import idv.fd.purchase.dto.*;
import idv.fd.purchase.model.PurchaseHistory;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;

@Tag(name = "Purchase", description = "The Purchase APIs")
public interface PurchaseApi {

    @Operation(summary = "Purchase a dish from a restaurant", tags = {"Purchase"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = PurchaseHistory.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @PostMapping("/purchases")
    Mono<PurchaseHistory> purchaseDish(@Valid @RequestBody Purchase purchase);


    @Operation(summary = "Find top x users by total transaction amount within a date range", tags = {"Purchase"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserTxAmount.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping("/transactions/top-users")
    Flux<UserTxAmount> findTopTxUsers(
            @Parameter(name = "top", description = "The specified top x (min = 1, max = 1000)") @Min(1) @Max(1000) int top,
            @Parameter(name = "fromDate", description = "The specified start date (format is MM/dd/yyyy), eg: 12/25/2019") LocalDate from,
            @Parameter(name = "toDate", description = "The specified end date (format is MM/dd/yyyy), eg: 02/14/2020") LocalDate to);


    @Operation(summary = "Find total number and dollar value of transactions that happened within a date range", tags = {"Purchase"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TxNumbAmount.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping("/transactions/sum")
    Flux<TxNumbAmount> findTxNumbAmount(
            @Parameter(name = "fromDate", description = "The specified start date (format is MM/dd/yyyy), eg: 12/25/2019") LocalDate from,
            @Parameter(name = "toDate", description = "The specified end date (format is MM/dd/yyyy), eg: 02/14/2020") LocalDate to);


    @Operation(summary = "Find most popular restaurants by transaction volume, either by number of transactions or transaction dollar value", tags = {"Purchase"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantTxAmount.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping("/transactions/max-restaurants")
    Flux<RestaurantTxAmount> findMaxTxRestaurants(
            @Parameter(name = "byAmount", description = "To indicate it's calculated by transaction amount or number of transactions") boolean byAmount);


    @Operation(summary = "Find total number of users who made transactions above or below $v within a date range", tags = {"Purchase"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = Count.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping("/transactions/user-count")
    Mono<Count> getUserCount(
            @Parameter(name = "amount", description = "The specified transaction amount") @Min(0) BigDecimal amount,
            @Parameter(name = "fromDate", description = "The specified start date (format is MM/dd/yyyy), eg: 12/25/2019") LocalDate from,
            @Parameter(name = "toDate", description = "The specified end date (format is MM/dd/yyyy), eg: 02/14/2020") LocalDate to,
            @Parameter(name = "lessThan", description = "To indicate less or more than the specified transaction amount") boolean lessThan);
}
