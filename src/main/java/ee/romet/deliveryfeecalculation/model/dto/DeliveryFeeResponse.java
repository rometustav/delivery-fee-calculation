package ee.romet.deliveryfeecalculation.model.dto;

import java.math.BigDecimal;

public record DeliveryFeeResponse(
        BigDecimal totalFee,
        String currency
) {
    public static DeliveryFeeResponse of(BigDecimal fee) {
        return new DeliveryFeeResponse(fee, "EUR");
    }
}