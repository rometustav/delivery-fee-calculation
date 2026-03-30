package ee.romet.deliveryfeecalculation.repository;

import ee.romet.deliveryfeecalculation.model.entity.ExtraFeeRule;
import ee.romet.deliveryfeecalculation.model.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExtraFeeRuleRepository extends JpaRepository<ExtraFeeRule, Long> {

    List<ExtraFeeRule> findByVehicleTypeAndValidFromLessThanEqualOrderByValidFromDesc(
            VehicleType vehicleType, LocalDateTime datetime);
}