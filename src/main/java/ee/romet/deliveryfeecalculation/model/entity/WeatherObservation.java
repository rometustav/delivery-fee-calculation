package ee.romet.deliveryfeecalculation.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_observation")
public class WeatherObservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String stationName;

    @Column(nullable = false)
    private String wmoCode;

    private Double airTemperature;
    private Double windSpeed;
    private String weatherPhenomenon;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public String getWmoCode() { return wmoCode; }
    public void setWmoCode(String wmoCode) { this.wmoCode = wmoCode; }

    public Double getAirTemperature() { return airTemperature; }
    public void setAirTemperature(Double airTemperature) { this.airTemperature = airTemperature; }

    public Double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(Double windSpeed) { this.windSpeed = windSpeed; }

    public String getWeatherPhenomenon() { return weatherPhenomenon; }
    public void setWeatherPhenomenon(String weatherPhenomenon) { this.weatherPhenomenon = weatherPhenomenon; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}