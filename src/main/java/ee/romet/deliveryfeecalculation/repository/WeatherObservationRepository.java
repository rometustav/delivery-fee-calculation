package ee.romet.deliveryfeecalculation.repository;

import ee.romet.deliveryfeecalculation.model.entity.WeatherObservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface WeatherObservationRepository extends JpaRepository<WeatherObservation, Long> {

    Optional<WeatherObservation> findTopByStationNameOrderByTimestampDesc(String stationName);

    Optional<WeatherObservation> findTopByStationNameAndTimestampLessThanEqualOrderByTimestampDesc(
            String stationName, LocalDateTime datetime);
}