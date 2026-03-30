package ee.romet.deliveryfeecalculation.service;

import ee.romet.deliveryfeecalculation.exception.ForbiddenVehicleException;
import ee.romet.deliveryfeecalculation.model.entity.BaseFeeRule;
import ee.romet.deliveryfeecalculation.model.entity.ExtraFeeRule;
import ee.romet.deliveryfeecalculation.model.entity.WeatherObservation;
import ee.romet.deliveryfeecalculation.model.enums.City;
import ee.romet.deliveryfeecalculation.model.enums.ExtraFeeType;
import ee.romet.deliveryfeecalculation.model.enums.VehicleType;
import ee.romet.deliveryfeecalculation.repository.BaseFeeRuleRepository;
import ee.romet.deliveryfeecalculation.repository.ExtraFeeRuleRepository;
import ee.romet.deliveryfeecalculation.repository.WeatherObservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryFeeServiceTest {

    @Mock
    private WeatherObservationRepository weatherRepository;

    @Mock
    private BaseFeeRuleRepository baseFeeRuleRepository;

    @Mock
    private ExtraFeeRuleRepository extraFeeRuleRepository;

    @InjectMocks
    private DeliveryFeeService service;

    private WeatherObservation obs;
    private BaseFeeRule baseFeeRule;

    @BeforeEach
    void setUp() {
        obs = new WeatherObservation();
        obs.setStationName("Tartu-Tõravere");
        obs.setAirTemperature(-2.1);
        obs.setWindSpeed(4.7);
        obs.setWeatherPhenomenon("Light snow shower");
        obs.setTimestamp(LocalDateTime.now());

        baseFeeRule = new BaseFeeRule();
        baseFeeRule.setCity(City.TARTU);
        baseFeeRule.setVehicleType(VehicleType.BIKE);
        baseFeeRule.setFee(new BigDecimal("2.5"));
        baseFeeRule.setValidFrom(LocalDateTime.of(2026, 1, 1, 0, 0));
    }

    @Test
    void carHasNoExtraFees() {
        BaseFeeRule carRule = new BaseFeeRule();
        carRule.setCity(City.TALLINN);
        carRule.setVehicleType(VehicleType.CAR);
        carRule.setFee(new BigDecimal("4.0"));
        carRule.setValidFrom(LocalDateTime.of(2026, 1, 1, 0, 0));

        obs.setAirTemperature(-15.0);
        obs.setWindSpeed(25.0);
        obs.setWeatherPhenomenon("Heavy snow");

        when(weatherRepository.findTopByStationNameOrderByTimestampDesc("Tallinn-Harku"))
                .thenReturn(Optional.of(obs));
        when(baseFeeRuleRepository.findTopByCityAndVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(City.TALLINN), eq(VehicleType.CAR), any()))
                .thenReturn(Optional.of(carRule));
        when(extraFeeRuleRepository.findByVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(VehicleType.CAR), any()))
                .thenReturn(List.of());

        var result = service.calculate(City.TALLINN, VehicleType.CAR, null);

        assertThat(result.totalFee()).isEqualByComparingTo(new BigDecimal("4.0"));
    }

    @Test
    void temperatureBelowMinus10_Adds1EUR() {
        obs.setAirTemperature(-15.0);
        obs.setWindSpeed(5.0);
        obs.setWeatherPhenomenon(null);

        when(weatherRepository.findTopByStationNameOrderByTimestampDesc("Tartu-Tõravere"))
                .thenReturn(Optional.of(obs));
        when(baseFeeRuleRepository.findTopByCityAndVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(City.TARTU), eq(VehicleType.BIKE), any()))
                .thenReturn(Optional.of(baseFeeRule));

        ExtraFeeRule coldRule = extraFeeRule(VehicleType.BIKE, ExtraFeeType.TEMPERATURE, null, -10.0, null, new BigDecimal("1.0"));
        when(extraFeeRuleRepository.findByVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(VehicleType.BIKE), any()))
                .thenReturn(List.of(coldRule));

        var result = service.calculate(City.TARTU, VehicleType.BIKE, null);

        assertThat(result.totalFee()).isEqualByComparingTo(new BigDecimal("3.5")); // RBF=2.5 + ATEF=1.0
    }

    @Test
    void temperatureBetweenMinus10And0_Adds05EUR() {
        obs.setAirTemperature(-5.0);
        obs.setWindSpeed(5.0);
        obs.setWeatherPhenomenon(null);

        when(weatherRepository.findTopByStationNameOrderByTimestampDesc("Tartu-Tõravere"))
                .thenReturn(Optional.of(obs));
        when(baseFeeRuleRepository.findTopByCityAndVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(City.TARTU), eq(VehicleType.BIKE), any()))
                .thenReturn(Optional.of(baseFeeRule));

        ExtraFeeRule coolRule = extraFeeRule(VehicleType.BIKE, ExtraFeeType.TEMPERATURE, -10.0, 0.0, null, new BigDecimal("0.5"));
        when(extraFeeRuleRepository.findByVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(VehicleType.BIKE), any()))
                .thenReturn(List.of(coolRule));

        var result = service.calculate(City.TARTU, VehicleType.BIKE, null);

        assertThat(result.totalFee()).isEqualByComparingTo(new BigDecimal("3.0")); // RBF=2.5 + ATEF=0.5
    }

    @Test
    void temperatureAbove0HasNoExtraFee() {
        obs.setAirTemperature(5.0);
        obs.setWindSpeed(5.0);
        obs.setWeatherPhenomenon(null);

        when(weatherRepository.findTopByStationNameOrderByTimestampDesc("Tartu-Tõravere"))
                .thenReturn(Optional.of(obs));
        when(baseFeeRuleRepository.findTopByCityAndVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(City.TARTU), eq(VehicleType.BIKE), any()))
                .thenReturn(Optional.of(baseFeeRule));

        ExtraFeeRule coolRule = extraFeeRule(VehicleType.BIKE, ExtraFeeType.TEMPERATURE, -10.0, 0.0, null, new BigDecimal("0.5"));
        when(extraFeeRuleRepository.findByVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(VehicleType.BIKE), any()))
                .thenReturn(List.of(coolRule));

        var result = service.calculate(City.TARTU, VehicleType.BIKE, null);

        assertThat(result.totalFee()).isEqualByComparingTo(new BigDecimal("2.5")); // RBF=2.5, no extra
    }

    @Test
    void windSpeedBetween10And20_Adds05EUR() {
        obs.setAirTemperature(5.0);
        obs.setWindSpeed(15.0);
        obs.setWeatherPhenomenon(null);

        when(weatherRepository.findTopByStationNameOrderByTimestampDesc("Tartu-Tõravere"))
                .thenReturn(Optional.of(obs));
        when(baseFeeRuleRepository.findTopByCityAndVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(City.TARTU), eq(VehicleType.BIKE), any()))
                .thenReturn(Optional.of(baseFeeRule));

        ExtraFeeRule windRule = extraFeeRule(VehicleType.BIKE, ExtraFeeType.WIND_SPEED, 10.0, 20.0, null, new BigDecimal("0.5"));
        when(extraFeeRuleRepository.findByVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(VehicleType.BIKE), any()))
                .thenReturn(List.of(windRule));

        var result = service.calculate(City.TARTU, VehicleType.BIKE, null);

        assertThat(result.totalFee()).isEqualByComparingTo(new BigDecimal("3.0")); // RBF=2.5 + WSEF=0.5
    }

    @Test
    void windSpeedBelow10HasNoExtraFee() {
        obs.setAirTemperature(5.0);
        obs.setWindSpeed(5.0);
        obs.setWeatherPhenomenon(null);

        when(weatherRepository.findTopByStationNameOrderByTimestampDesc("Tartu-Tõravere"))
                .thenReturn(Optional.of(obs));
        when(baseFeeRuleRepository.findTopByCityAndVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(City.TARTU), eq(VehicleType.BIKE), any()))
                .thenReturn(Optional.of(baseFeeRule));

        ExtraFeeRule windRule = extraFeeRule(VehicleType.BIKE, ExtraFeeType.WIND_SPEED, 10.0, 20.0, null, new BigDecimal("0.5"));
        when(extraFeeRuleRepository.findByVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(VehicleType.BIKE), any()))
                .thenReturn(List.of(windRule));

        var result = service.calculate(City.TARTU, VehicleType.BIKE, null);

        assertThat(result.totalFee()).isEqualByComparingTo(new BigDecimal("2.5")); // RBF=2.5, no extra
    }

    @Test
    void throwsForbiddenWhenWindTooHigh() {
        obs.setWindSpeed(25.0);
        obs.setWeatherPhenomenon(null);

        when(weatherRepository.findTopByStationNameOrderByTimestampDesc("Tartu-Tõravere"))
                .thenReturn(Optional.of(obs));
        when(baseFeeRuleRepository.findTopByCityAndVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(City.TARTU), eq(VehicleType.BIKE), any()))
                .thenReturn(Optional.of(baseFeeRule));

        ExtraFeeRule forbiddenWind = extraFeeRule(VehicleType.BIKE, ExtraFeeType.WIND_SPEED, 20.0, null, null, null);
        when(extraFeeRuleRepository.findByVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(VehicleType.BIKE), any()))
                .thenReturn(List.of(forbiddenWind));

        assertThatThrownBy(() -> service.calculate(City.TARTU, VehicleType.BIKE, null))
                .isInstanceOf(ForbiddenVehicleException.class);
    }

    @Test
    void snowPhenomenon_Adds1EUR() {
        obs.setAirTemperature(5.0);
        obs.setWindSpeed(5.0);
        obs.setWeatherPhenomenon("Light snow shower");

        when(weatherRepository.findTopByStationNameOrderByTimestampDesc("Tartu-Tõravere"))
                .thenReturn(Optional.of(obs));
        when(baseFeeRuleRepository.findTopByCityAndVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(City.TARTU), eq(VehicleType.BIKE), any()))
                .thenReturn(Optional.of(baseFeeRule));

        ExtraFeeRule snowRule = extraFeeRule(VehicleType.BIKE, ExtraFeeType.PHENOMENON, null, null, "snow", new BigDecimal("1.0"));
        when(extraFeeRuleRepository.findByVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(VehicleType.BIKE), any()))
                .thenReturn(List.of(snowRule));

        var result = service.calculate(City.TARTU, VehicleType.BIKE, null);

        assertThat(result.totalFee()).isEqualByComparingTo(new BigDecimal("3.5")); // RBF=2.5 + WPEF=1.0
    }

    @Test
    void sleetPhenomenon_Adds1EUR() {
        obs.setAirTemperature(5.0);
        obs.setWindSpeed(5.0);
        obs.setWeatherPhenomenon("Light sleet");

        when(weatherRepository.findTopByStationNameOrderByTimestampDesc("Tartu-Tõravere"))
                .thenReturn(Optional.of(obs));
        when(baseFeeRuleRepository.findTopByCityAndVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(City.TARTU), eq(VehicleType.BIKE), any()))
                .thenReturn(Optional.of(baseFeeRule));

        ExtraFeeRule sleetRule = extraFeeRule(VehicleType.BIKE, ExtraFeeType.PHENOMENON, null, null, "sleet", new BigDecimal("1.0"));
        when(extraFeeRuleRepository.findByVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(VehicleType.BIKE), any()))
                .thenReturn(List.of(sleetRule));

        var result = service.calculate(City.TARTU, VehicleType.BIKE, null);

        assertThat(result.totalFee()).isEqualByComparingTo(new BigDecimal("3.5")); // RBF=2.5 + WPEF=1.0
    }

    @Test
    void rainPhenomenon_Adds05EUR() {
        obs.setAirTemperature(5.0);
        obs.setWindSpeed(5.0);
        obs.setWeatherPhenomenon("Moderate rain");

        when(weatherRepository.findTopByStationNameOrderByTimestampDesc("Tartu-Tõravere"))
                .thenReturn(Optional.of(obs));
        when(baseFeeRuleRepository.findTopByCityAndVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(City.TARTU), eq(VehicleType.BIKE), any()))
                .thenReturn(Optional.of(baseFeeRule));

        ExtraFeeRule rainRule = extraFeeRule(VehicleType.BIKE, ExtraFeeType.PHENOMENON, null, null, "rain", new BigDecimal("0.5"));
        when(extraFeeRuleRepository.findByVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(VehicleType.BIKE), any()))
                .thenReturn(List.of(rainRule));

        var result = service.calculate(City.TARTU, VehicleType.BIKE, null);

        assertThat(result.totalFee()).isEqualByComparingTo(new BigDecimal("3.0")); // RBF=2.5 + WPEF=0.5
    }

    @Test
    void glazePhenomenonIsForbidden() {
        obs.setWindSpeed(5.0);
        obs.setWeatherPhenomenon("Glaze");

        when(weatherRepository.findTopByStationNameOrderByTimestampDesc("Tartu-Tõravere"))
                .thenReturn(Optional.of(obs));
        when(baseFeeRuleRepository.findTopByCityAndVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(City.TARTU), eq(VehicleType.BIKE), any()))
                .thenReturn(Optional.of(baseFeeRule));

        ExtraFeeRule glazeRule = extraFeeRule(VehicleType.BIKE, ExtraFeeType.PHENOMENON, null, null, "glaze", null);
        when(extraFeeRuleRepository.findByVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(VehicleType.BIKE), any()))
                .thenReturn(List.of(glazeRule));

        assertThatThrownBy(() -> service.calculate(City.TARTU, VehicleType.BIKE, null))
                .isInstanceOf(ForbiddenVehicleException.class);
    }

    @Test
    void hailPhenomenonIsForbidden() {
        obs.setAirTemperature(5.0);
        obs.setWindSpeed(5.0);
        obs.setWeatherPhenomenon("Hail");

        when(weatherRepository.findTopByStationNameOrderByTimestampDesc("Tartu-Tõravere"))
                .thenReturn(Optional.of(obs));
        when(baseFeeRuleRepository.findTopByCityAndVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(City.TARTU), eq(VehicleType.BIKE), any()))
                .thenReturn(Optional.of(baseFeeRule));

        ExtraFeeRule hailRule = extraFeeRule(VehicleType.BIKE, ExtraFeeType.PHENOMENON, null, null, "hail", null);
        when(extraFeeRuleRepository.findByVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(VehicleType.BIKE), any()))
                .thenReturn(List.of(hailRule));

        assertThatThrownBy(() -> service.calculate(City.TARTU, VehicleType.BIKE, null))
                .isInstanceOf(ForbiddenVehicleException.class);
    }

    @Test
    void thunderPhenomenonIsForbidden() {
        obs.setAirTemperature(5.0);
        obs.setWindSpeed(5.0);
        obs.setWeatherPhenomenon("Thunder");

        when(weatherRepository.findTopByStationNameOrderByTimestampDesc("Tartu-Tõravere"))
                .thenReturn(Optional.of(obs));
        when(baseFeeRuleRepository.findTopByCityAndVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(City.TARTU), eq(VehicleType.BIKE), any()))
                .thenReturn(Optional.of(baseFeeRule));

        ExtraFeeRule thunderRule = extraFeeRule(VehicleType.BIKE, ExtraFeeType.PHENOMENON, null, null, "thunder", null);
        when(extraFeeRuleRepository.findByVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(VehicleType.BIKE), any()))
                .thenReturn(List.of(thunderRule));

        assertThatThrownBy(() -> service.calculate(City.TARTU, VehicleType.BIKE, null))
                .isInstanceOf(ForbiddenVehicleException.class);
    }

    @Test
    void throwsWhenNoWeatherData() {
        when(weatherRepository.findTopByStationNameOrderByTimestampDesc("Tartu-Tõravere"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.calculate(City.TARTU, VehicleType.BIKE, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No weather data available");
    }

    @Test
    void usesHistoricalWeatherAndRules() {
        LocalDateTime historical = LocalDateTime.of(2026, 1, 1, 12, 0);

        when(weatherRepository.findTopByStationNameAndTimestampLessThanEqualOrderByTimestampDesc(
                "Tartu-Tõravere", historical))
                .thenReturn(Optional.of(obs));
        when(baseFeeRuleRepository.findTopByCityAndVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(City.TARTU), eq(VehicleType.BIKE), eq(historical)))
                .thenReturn(Optional.of(baseFeeRule));
        when(extraFeeRuleRepository.findByVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
                eq(VehicleType.BIKE), eq(historical)))
                .thenReturn(List.of());

        var result = service.calculate(City.TARTU, VehicleType.BIKE, historical);

        assertThat(result.totalFee()).isEqualByComparingTo(new BigDecimal("2.5"));
    }

    private ExtraFeeRule extraFeeRule(VehicleType vehicleType, ExtraFeeType type,
                                      Double conditionMin, Double conditionMax,
                                      String phenomenonKeyword, BigDecimal fee) {
        ExtraFeeRule rule = new ExtraFeeRule();
        rule.setVehicleType(vehicleType);
        rule.setType(type);
        rule.setConditionMin(conditionMin);
        rule.setConditionMax(conditionMax);
        rule.setPhenomenonKeyword(phenomenonKeyword);
        rule.setFee(fee);
        rule.setValidFrom(LocalDateTime.of(2026, 1, 1, 0, 0));
        return rule;
    }
}