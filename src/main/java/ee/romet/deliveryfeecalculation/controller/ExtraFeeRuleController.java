package ee.romet.deliveryfeecalculation.controller;

import ee.romet.deliveryfeecalculation.model.entity.ExtraFeeRule;
import ee.romet.deliveryfeecalculation.repository.ExtraFeeRuleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rules/extra-fees")
@Tag(name = "Fee Rules", description = "Manage base and extra fee rules")
public class ExtraFeeRuleController {

    private final ExtraFeeRuleRepository repository;

    public ExtraFeeRuleController(ExtraFeeRuleRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @Operation(summary = "Get all extra fee rules")
    public List<ExtraFeeRule> getAll() {
        return repository.findAll();
    }

    @PostMapping
    @Operation(summary = "Create a new extra fee rule")
    public ResponseEntity<ExtraFeeRule> create(@RequestBody @Valid ExtraFeeRule rule) {
        return ResponseEntity.ok(repository.save(rule));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an extra fee rule")
    public ResponseEntity<ExtraFeeRule> update(@PathVariable Long id, @RequestBody @Valid ExtraFeeRule rule) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        rule.setId(id);
        return ResponseEntity.ok(repository.save(rule));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an extra fee rule")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}