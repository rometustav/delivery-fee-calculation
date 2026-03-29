package ee.romet.deliveryfeecalculation.model.dto;

import ee.romet.deliveryfeecalculation.model.enums.City;
import ee.romet.deliveryfeecalculation.model.enums.VehicleType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record DeliveryFeeRequest(
        @NotNull City city,
        @NotNull VehicleType vehicleType,
        LocalDateTime datetime
) {}