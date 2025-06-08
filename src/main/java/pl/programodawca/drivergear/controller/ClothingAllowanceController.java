package pl.programodawca.drivergear.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.programodawca.drivergear.dto.ClothingAllowanceDTO;
import pl.programodawca.drivergear.model.AllowanceStatus;
import pl.programodawca.drivergear.service.ClothingAllowanceService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clothing-allowances")
@RequiredArgsConstructor
public class ClothingAllowanceController {
    private final ClothingAllowanceService allowanceService;

    @PostMapping
    public ResponseEntity<ClothingAllowanceDTO> createAllowance(
            @RequestParam Long employeeId,
            @RequestParam Long positionId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) String notes) {
        ClothingAllowanceDTO allowance = allowanceService.createAllowance(employeeId, positionId, startDate, endDate, notes);
        return new ResponseEntity<>(allowance, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClothingAllowanceDTO> getAllowanceById(@PathVariable("id") Long allowanceId) {
        ClothingAllowanceDTO allowance = allowanceService.getAllowanceById(allowanceId);
        return ResponseEntity.ok(allowance);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ClothingAllowanceDTO>> getAllowancesByEmployeeId(@PathVariable Long employeeId) {
        List<ClothingAllowanceDTO> allowances = allowanceService.getAllowancesByEmployeeId(employeeId);
        return ResponseEntity.ok(allowances);
    }

    @GetMapping("/employee/{employeeId}/status/{status}")
    public ResponseEntity<List<ClothingAllowanceDTO>> getAllowancesByEmployeeIdAndStatus(
            @PathVariable Long employeeId,
            @PathVariable AllowanceStatus status) {
        List<ClothingAllowanceDTO> allowances = allowanceService.getAllowancesByEmployeeIdAndStatus(employeeId, status);
        return ResponseEntity.ok(allowances);
    }

    @GetMapping("/position/{positionId}")
    public ResponseEntity<List<ClothingAllowanceDTO>> getAllowancesByPositionId(@PathVariable Long positionId) {
        List<ClothingAllowanceDTO> allowances = allowanceService.getAllowancesByPositionId(positionId);
        return ResponseEntity.ok(allowances);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ClothingAllowanceDTO>> getAllowancesByStatus(@PathVariable AllowanceStatus status) {
        List<ClothingAllowanceDTO> allowances = allowanceService.getAllowancesByStatus(status);
        return ResponseEntity.ok(allowances);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ClothingAllowanceDTO>> getActiveAllowancesOnDate(@RequestParam(required = false) LocalDate date) {
        LocalDate checkDate = date != null ? date : LocalDate.now();
        List<ClothingAllowanceDTO> allowances = allowanceService.getActiveAllowancesOnDate(checkDate);
        return ResponseEntity.ok(allowances);
    }

    @GetMapping("/expired")
    public ResponseEntity<List<ClothingAllowanceDTO>> getExpiredAllowances(@RequestParam(required = false) LocalDate date) {
        LocalDate checkDate = date != null ? date : LocalDate.now();
        List<ClothingAllowanceDTO> allowances = allowanceService.getExpiredAllowances(checkDate);
        return ResponseEntity.ok(allowances);
    }

    @PostMapping("/update-expired")
    public ResponseEntity<Map<String, Integer>> updateExpiredAllowances() {
        int count = allowanceService.updateExpiredAllowances();
        return ResponseEntity.ok(Map.of("updatedCount", count));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ClothingAllowanceDTO> updateAllowanceStatus(
            @PathVariable("id") Long allowanceId,
            @RequestParam AllowanceStatus status) {
        ClothingAllowanceDTO allowance = allowanceService.updateAllowanceStatus(allowanceId, status);
        return ResponseEntity.ok(allowance);
    }

    @PatchMapping("/{id}/dates")
    public ResponseEntity<ClothingAllowanceDTO> updateAllowanceDates(
            @PathVariable("id") Long allowanceId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        ClothingAllowanceDTO allowance = allowanceService.updateAllowanceDates(allowanceId, startDate, endDate);
        return ResponseEntity.ok(allowance);
    }

    @PatchMapping("/{id}/notes")
    public ResponseEntity<ClothingAllowanceDTO> updateAllowanceNotes(
            @PathVariable("id") Long allowanceId,
            @RequestParam String notes) {
        ClothingAllowanceDTO allowance = allowanceService.updateAllowanceNotes(allowanceId, notes);
        return ResponseEntity.ok(allowance);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ClothingAllowanceDTO> cancelAllowance(
            @PathVariable("id") Long allowanceId,
            @RequestParam String reason) {
        ClothingAllowanceDTO allowance = allowanceService.cancelAllowance(allowanceId, reason);
        return ResponseEntity.ok(allowance);
    }
}
