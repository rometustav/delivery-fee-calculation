package ee.romet.deliveryfeecalculation.repository;

import ee.romet.deliveryfeecalculation.model.entity.BaseFeeRule;
import ee.romet.deliveryfeecalculation.model.enums.City;
import ee.romet.deliveryfeecalculation.model.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BaseFeeRuleRepository extends JpaRepository<BaseFeeRule, Long> {

    Optional<BaseFeeRule> findTopByCityAndVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
            City city, VehicleType vehicleType, LocalDateTime datetime);
}