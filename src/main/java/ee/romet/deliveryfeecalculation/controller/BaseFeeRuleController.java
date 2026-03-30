package ee.romet.deliveryfeecalculation.controller;

import ee.romet.deliveryfeecalculation.model.entity.BaseFeeRule;
import ee.romet.deliveryfeecalculation.repository.BaseFeeRuleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rules/base-fees")
@Tag(name = "Fee Rules", description = "Manage base and extra fee rules")
public class BaseFeeRuleController {

    private final BaseFeeRuleRepository repository;

    public BaseFeeRuleController(BaseFeeRuleRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @Operation(summary = "Get all base fee rules")
    public List<BaseFeeRule> getAll() {
        return repository.findAll();
    }

    @PostMapping
    @Operation(summary = "Create a new base fee rule")
    public ResponseEntity<BaseFeeRule> create(@RequestBody @Valid BaseFeeRule rule) {
        return ResponseEntity.ok(repository.save(rule));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a base fee rule")
    public ResponseEntity<BaseFeeRule> update(@PathVariable Long id, @RequestBody @Valid BaseFeeRule rule) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        rule.setId(id);
        return ResponseEntity.ok(repository.save(rule));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a base fee rule")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}