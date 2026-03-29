package ee.romet.deliveryfeecalculation.controller;

import ee.romet.deliveryfeecalculation.model.dto.DeliveryFeeResponse;
import ee.romet.deliveryfeecalculation.model.enums.City;
import ee.romet.deliveryfeecalculation.model.enums.VehicleType;
import ee.romet.deliveryfeecalculation.service.DeliveryFeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Delivery Fee", description = "Delivery fee calculation based on city, vehicle type and weather conditions")
public class DeliveryFeeController {

    private final DeliveryFeeService deliveryFeeService;

    public DeliveryFeeController(DeliveryFeeService deliveryFeeService) {
        this.deliveryFeeService = deliveryFeeService;
    }

    /**
     * Calculates the delivery fee for given city and vehicle
     * using DeliveryFeeService.
     * Optionally accepts a datetime for historical calculations.
     *
     * @param city          delivery city (TALLINN, TARTU, PARNU)
     * @param vehicleType   vehicle type (CAR, SCOOTER, BIKE)
     * @param datetime      optional datetime for historical fee calculation
     * @return              total delivery fee in EUR
     */
    @GetMapping("/delivery-fee")
    @Operation(
            summary = "Calculate delivery fee",
            description = "Returns total fee based on city, vehicle type and latest weather data. Optionally provide a datetime for historical calculation."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fee calculated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or forbidden vehicle type")
    })
    public ResponseEntity<DeliveryFeeResponse> getDeliveryFee(
            @Parameter(description = "City: TALLINN, TARTU or PARNU")
            @RequestParam @NotNull City city,

            @Parameter(description = "Vehicle type: CAR, SCOOTER or BIKE")
            @RequestParam @NotNull VehicleType vehicleType,

            @Parameter(description = "Optional datetime for historical calculation. Format (ISO8601): 2026-03-29T14:00:00")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime datetime
    ) {
        return ResponseEntity.ok(deliveryFeeService.calculate(city, vehicleType, datetime));
    }
}