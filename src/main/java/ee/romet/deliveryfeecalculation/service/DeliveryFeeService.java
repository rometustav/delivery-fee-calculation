package ee.romet.deliveryfeecalculation.service;

import ee.romet.deliveryfeecalculation.exception.ForbiddenVehicleException;
import ee.romet.deliveryfeecalculation.model.dto.DeliveryFeeResponse;
import ee.romet.deliveryfeecalculation.model.entity.WeatherObservation;
import ee.romet.deliveryfeecalculation.model.enums.City;
import ee.romet.deliveryfeecalculation.model.enums.VehicleType;
import ee.romet.deliveryfeecalculation.repository.WeatherObservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DeliveryFeeService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryFeeService.class);

    private final WeatherObservationRepository repository;

    public DeliveryFeeService(WeatherObservationRepository repository) {
        this.repository = repository;
    }

    /**
     * Calculates the total delivery fee based on 3 factors:
     * city, vehicle type, and weather conditions
     * at the given time (or latest if not specified).
     *
     * @param city          delivery city
     * @param vehicleType   courier vehicle type
     * @param datetime      optional datetime for historical calculation
     * @return              delivery fee response with total fee in EUR
     */
    public DeliveryFeeResponse calculate(City city, VehicleType vehicleType, LocalDateTime datetime) {
        log.info("Calculating fee |  city={}, vehicle={}, datetime={}", city, vehicleType, datetime);

        // latest weather information
        WeatherObservation obs = getObservation(city, datetime);

        // extra fees
        BigDecimal RBF = getRegionalBaseFee(city, vehicleType);
        BigDecimal ATEF = getAirTemperatureExtraFee(obs, vehicleType);
        BigDecimal WSEF = getWindSpeedExtraFee(obs, vehicleType);
        BigDecimal WPEF = getWeatherPhenomenonExtraFee(obs, vehicleType);

        BigDecimal total = RBF.add(ATEF).add(WSEF).add(WPEF);
        log.info("Fee breakdown | RBF={}, ATEF={}, WSEF={}, WPEF={}, Total={}", RBF, ATEF, WSEF, WPEF, total);

        return DeliveryFeeResponse.of(total);
    }

    private WeatherObservation getObservation(City city, LocalDateTime datetime) {
        Optional<WeatherObservation> obs = datetime == null
                ? repository.findTopByStationNameOrderByTimestampDesc(city.getStationName())
                : repository.findTopByStationNameAndTimestampLessThanEqualOrderByTimestampDesc(
                city.getStationName(), datetime);

        return obs.orElseThrow(() -> {
            log.warn("No weather observation found for city={}", city.getStationName());
            return new IllegalStateException("No weather data available for city " + city.getStationName());
        });
    }


    /* ---- Helper functions for business logic calculations ---- */

    private BigDecimal getRegionalBaseFee(City city, VehicleType vehicleType) {
        return switch (city) {
            case TALLINN -> switch (vehicleType) {
                case CAR -> new BigDecimal("4.0");
                case SCOOTER -> new BigDecimal("3.5");
                case BIKE -> new BigDecimal("3.0");
            };
            case TARTU -> switch (vehicleType) {
                case CAR -> new BigDecimal("3.5");
                case SCOOTER -> new BigDecimal("3.0");
                case BIKE -> new BigDecimal("2.5");
            };
            case PARNU -> switch (vehicleType) {
                case CAR -> new BigDecimal("3.0");
                case SCOOTER -> new BigDecimal("2.5");
                case BIKE -> new BigDecimal("2.0");
            };
        };
    }

    private BigDecimal getAirTemperatureExtraFee(WeatherObservation obs, VehicleType vehicleType) {
        if (vehicleType == VehicleType.CAR) return BigDecimal.ZERO;
        if (obs.getAirTemperature() == null) return BigDecimal.ZERO;

        double temp = obs.getAirTemperature();
        if (temp < -10) return new BigDecimal("1.0");
        if (temp <= 0) return new BigDecimal("0.5");
        return BigDecimal.ZERO;
    }

    private BigDecimal getWindSpeedExtraFee(WeatherObservation obs, VehicleType vehicleType) {
        if (vehicleType != VehicleType.BIKE) return BigDecimal.ZERO;
        if (obs.getWindSpeed() == null) return BigDecimal.ZERO;

        double wind = obs.getWindSpeed();
        if (wind > 20) throw new ForbiddenVehicleException();
        if (wind >= 10) return new BigDecimal("0.5");
        return BigDecimal.ZERO;
    }

    private BigDecimal getWeatherPhenomenonExtraFee(WeatherObservation obs, VehicleType vehicleType) {
        if (vehicleType == VehicleType.CAR) return BigDecimal.ZERO;
        if (obs.getWeatherPhenomenon() == null) return BigDecimal.ZERO;

        String phenomenon = obs.getWeatherPhenomenon().toLowerCase();

        if (phenomenon.contains("glaze") || phenomenon.contains("hail") || phenomenon.contains("thunder")) {
            throw new ForbiddenVehicleException();
        }
        if (phenomenon.contains("snow") || phenomenon.contains("sleet")) {
            return new BigDecimal("1.0");
        }
        if (phenomenon.contains("rain")) {
            return new BigDecimal("0.5");
        }
        return BigDecimal.ZERO;
    }
}