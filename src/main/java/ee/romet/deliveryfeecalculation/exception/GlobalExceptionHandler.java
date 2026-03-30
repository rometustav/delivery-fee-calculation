package ee.romet.deliveryfeecalculation.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ee.romet.deliveryfeecalculation.model.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles forbidden vehicle type exceptions from business logic
    @ExceptionHandler(ForbiddenVehicleException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenVehicleException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    // Handles invalid enum values for city or vehicle type request parameters
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleBadEnum(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse("Invalid value for parameter: " + ex.getName()));
    }

    // Handles missing weather data for the requested city
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleNoWeatherData(IllegalStateException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    // Handles missing parameters
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("Missing required parameter: " + ex.getParameterName()));
    }
}