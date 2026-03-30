package ee.romet.deliveryfeecalculation.service;

import ee.romet.deliveryfeecalculation.exception.ForbiddenVehicleException;
import ee.romet.deliveryfeecalculation.model.dto.DeliveryFeeResponse;
import ee.romet.deliveryfeecalculation.model.entity.ExtraFeeRule;
import ee.romet.deliveryfeecalculation.model.entity.WeatherObservation;
import ee.romet.deliveryfeecalculation.model.enums.City;
import ee.romet.deliveryfeecalculation.model.enums.VehicleType;
import ee.romet.deliveryfeecalculation.repository.BaseFeeRuleRepository;
import ee.romet.deliveryfeecalculation.repository.ExtraFeeRuleRepository;
import ee.romet.deliveryfeecalculation.repository.WeatherObservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryFeeService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryFeeService.class);

    private final WeatherObservationRepository weatherRepository;
    private final BaseFeeRuleRepository baseFeeRuleRepository;
    private final ExtraFeeRuleRepository extraFeeRuleRepository;

    public DeliveryFeeService(WeatherObservationRepository weatherRepository, BaseFeeRuleRepository baseFeeRuleRepository, ExtraFeeRuleRepository extraFeeRuleRepository) {
        this.weatherRepository = weatherRepository;
        this.baseFeeRuleRepository = baseFeeRuleRepository;
        this.extraFeeRuleRepository = extraFeeRuleRepository;
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

        LocalDateTime calculationTime = datetime != null ? datetime : LocalDateTime.now();
        WeatherObservation obs = getObservation(city, datetime);

        BigDecimal RBF = getRegionalBaseFee(city, vehicleType, calculationTime);
        BigDecimal EF = getExtraFees(obs, vehicleType, calculationTime);

        BigDecimal total = RBF.add(EF);
        log.info("Fee breakdown | RBF={}, EF={}, Total={}", RBF, EF, total);

        return DeliveryFeeResponse.of(total);
    }

    private WeatherObservation getObservation(City city, LocalDateTime datetime) {
        Optional<WeatherObservation> obs = datetime == null
                ? weatherRepository.findTopByStationNameOrderByTimestampDesc(city.getStationName())
                : weatherRepository.findTopByStationNameAndTimestampLessThanEqualOrderByTimestampDesc(
                city.getStationName(), datetime);

        return obs.orElseThrow(() -> {
            log.warn("No weather observation found for city={}", city.getStationName());
            return new IllegalStateException("No weather data available for " + city.getStationName());
        });
    }


    /* ---- Helper functions for business logic calculations ---- */

    private BigDecimal getRegionalBaseFee(City city, VehicleType vehicleType, LocalDateTime calculationTime) {
        return baseFeeRuleRepository
                .findTopByCityAndVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                        city, vehicleType, calculationTime)
                .orElseThrow(() -> new IllegalStateException(
                        "No base fee rule found for city=" + city + " and vehicle=" + vehicleType))
                .getFee();
    }

    private BigDecimal getExtraFees(WeatherObservation obs, VehicleType vehicleType, LocalDateTime calculationTime) {
        List<ExtraFeeRule> rules = extraFeeRuleRepository
                .findByVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(vehicleType, calculationTime);

        BigDecimal totalEF = BigDecimal.ZERO;

        for (ExtraFeeRule rule : rules) {
            BigDecimal fee = evaluateExtraFeeRule(rule, obs);
            if (fee != null) totalEF = totalEF.add(fee);
        }

        return totalEF;
    }

    private BigDecimal evaluateExtraFeeRule(ExtraFeeRule rule, WeatherObservation obs) {
        return switch (rule.getType()) {
            case TEMPERATURE -> evaluateTemperatureRule(rule, obs.getAirTemperature());
            case WIND_SPEED -> evaluateWindSpeedRule(rule, obs.getWindSpeed());
            case PHENOMENON -> evaluatePhenomenonRule(rule, obs.getWeatherPhenomenon());
        };
    }

    private BigDecimal evaluateTemperatureRule(ExtraFeeRule rule, Double temperature) {
        if (temperature == null) return null;
        boolean matches = (rule.getConditionMax() != null && temperature < rule.getConditionMax())
                && (rule.getConditionMin() == null || temperature >= rule.getConditionMin());
        if (!matches) return null;
        return rule.getFee();
    }

    private BigDecimal evaluateWindSpeedRule(ExtraFeeRule rule, Double windSpeed) {
        if (windSpeed == null) return null;
        boolean matches = rule.getConditionMax() == null
                ? windSpeed > rule.getConditionMin()                                          // forbidden: > 20
                : windSpeed >= rule.getConditionMin() && windSpeed <= rule.getConditionMax(); // range: 10-20
        if (!matches) return null;
        if (rule.getFee() == null) throw new ForbiddenVehicleException();
        return rule.getFee();
    }

    private BigDecimal evaluatePhenomenonRule(ExtraFeeRule rule, String phenomenon) {
        if (phenomenon == null) return null;
        if (!phenomenon.toLowerCase().contains(rule.getPhenomenonKeyword())) return null;
        if (rule.getFee() == null) throw new ForbiddenVehicleException();
        return rule.getFee();
    }
}