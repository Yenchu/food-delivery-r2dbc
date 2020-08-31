package idv.fd.restaurant.api;

import idv.fd.error.AppError;
import idv.fd.restaurant.model.OpenHours;
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
import reactor.core.publisher.Flux;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Tag(name = "OpenHours", description = "The Open Hours APIs")
public interface OpenHoursApi {

    @Operation(summary = "Find open hours by pagination", tags = {"OpenHours"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OpenHours.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping("/open-hours")
    Flux<OpenHours> findOpenHours(
            @Parameter(name = "page", description = "The specified page, start from `0`") int page,
            @Parameter(name = "size", description = "The number of records in the page") int size);


    @Operation(summary = "Find open hours by time", tags = {"OpenHours"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OpenHours.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping("/open-hours/findByTime")
    Flux<OpenHours> findOpenHoursByTime(
            @Parameter(name = "time", description = "The specified time (format is HH:mm), eg: 18:30") String timeStr,
            @Parameter(name = "dayOfWeek", description = "A day of week (Sun = 0, Sat = 6)") @Min(0) @Max(6) Integer dayOfWeek);


    @Operation(summary = "Find open hours by restaurant ID", tags = {"OpenHours"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OpenHours.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping("/open-hours/findByRestaurant")
    Flux<OpenHours> findOpenHoursByRestaurant(
            @Parameter(name = "restaurantId", description = "The specified restaurant ID") Long restaurantId);
}
