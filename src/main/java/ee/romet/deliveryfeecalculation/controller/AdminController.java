package ee.romet.deliveryfeecalculation.controller;

import ee.romet.deliveryfeecalculation.model.entity.WeatherObservation;
import ee.romet.deliveryfeecalculation.repository.WeatherObservationRepository;
import ee.romet.deliveryfeecalculation.scheduler.WeatherImportJob;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "Administrative endpoints")
public class AdminController {

    private final WeatherImportJob weatherImportJob;
    private final WeatherObservationRepository weatherRepository;

    public AdminController(WeatherImportJob weatherImportJob, WeatherObservationRepository weatherRepository) {
        this.weatherImportJob = weatherImportJob;
        this.weatherRepository = weatherRepository;
    }

    @PostMapping("/import-weather")
    @Operation(summary = "Manually trigger weather import")
    public ResponseEntity<String> triggerImport() {
        weatherImportJob.importWeather();
        return ResponseEntity.ok("Weather import triggered successfully");
    }

    @GetMapping("/weather-observations")
    @Operation(summary = "Get all weather observations")
    public List<WeatherObservation> getObservations() {
        return weatherRepository.findAll();
    }
}