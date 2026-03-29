package ee.romet.deliveryfeecalculation.exception;

public class ForbiddenVehicleException extends RuntimeException {
    public ForbiddenVehicleException() {
        super("Usage of this vehicle type is forbidden");
    }
}