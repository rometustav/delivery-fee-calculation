package ee.romet.deliveryfeecalculation.model.entity;

import ee.romet.deliveryfeecalculation.model.enums.ExtraFeeType;
import ee.romet.deliveryfeecalculation.model.enums.VehicleType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "extra_fee_rule")
public class ExtraFeeRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExtraFeeType type;

    private Double conditionMin;
    private Double conditionMax;
    private String phenomenonKeyword;

    // null fee = forbidden
    private BigDecimal fee;

    @Column(nullable = false)
    private LocalDateTime validFrom;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }

    public ExtraFeeType getType() { return type; }
    public void setType(ExtraFeeType type) { this.type = type; }

    public Double getConditionMin() { return conditionMin; }
    public void setConditionMin(Double conditionMin) { this.conditionMin = conditionMin; }

    public Double getConditionMax() { return conditionMax; }
    public void setConditionMax(Double conditionMax) { this.conditionMax = conditionMax; }

    public String getPhenomenonKeyword() { return phenomenonKeyword; }
    public void setPhenomenonKeyword(String phenomenonKeyword) { this.phenomenonKeyword = phenomenonKeyword; }

    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }

    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }
}