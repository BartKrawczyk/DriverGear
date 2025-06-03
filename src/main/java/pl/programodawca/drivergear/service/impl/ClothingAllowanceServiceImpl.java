package pl.programodawca.drivergear.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.programodawca.drivergear.dto.ClothingAllowanceDTO;
import pl.programodawca.drivergear.exception.BusinessException;
import pl.programodawca.drivergear.exception.EntityNotFoundException;
import pl.programodawca.drivergear.model.AllowanceStatus;
import pl.programodawca.drivergear.model.ClothingAllowance;
import pl.programodawca.drivergear.model.Employee;
import pl.programodawca.drivergear.model.Position;
import pl.programodawca.drivergear.repository.ClothingAllowanceRepository;
import pl.programodawca.drivergear.repository.EmployeeRepository;
import pl.programodawca.drivergear.repository.PositionRepository;
import pl.programodawca.drivergear.service.ClothingAllowanceService;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ClothingAllowanceServiceImpl implements ClothingAllowanceService {
    private final ClothingAllowanceRepository allowanceRepository;
    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;

    @Override
    public ClothingAllowanceDTO createAllowance(Long employeeId, Long positionId, LocalDate startDate, LocalDate endDate, String notes) {
        // Sprawdzenie czy pracownik istnieje
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Pracownik o ID " + employeeId + " nie został znaleziony"));

        // Sprawdzenie czy stanowisko istnieje
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new EntityNotFoundException("Stanowisko o ID " + positionId + " nie zostało znalezione"));

        // Walidacja dat
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Data rozpoczęcia nie może być późniejsza niż data zakończenia");
        }

        // Utworzenie nowego przydziału
        ClothingAllowance allowance = new ClothingAllowance();
        allowance.setEmployee(employee);
        allowance.setPosition(position);
        allowance.setStartDate(startDate);
        allowance.setEndDate(endDate);
        allowance.setStatus(AllowanceStatus.ACTIVE);
        allowance.setNotes(notes);

        // Zapisanie i konwersja na DTO
        ClothingAllowance savedAllowance = allowanceRepository.save(allowance);
        return ClothingAllowanceDTO.fromEntity(savedAllowance);
    }

    @Override
    public ClothingAllowanceDTO getAllowanceById(Long allowanceId) {
        ClothingAllowance allowance = allowanceRepository.findById(allowanceId)
                .orElseThrow(() -> new EntityNotFoundException("Przydział odzieżowy o ID " + allowanceId + " nie został znaleziony"));
        return ClothingAllowanceDTO.fromEntity(allowance);
    }

    @Override
    public List<ClothingAllowanceDTO> getAllowancesByEmployeeId(Long employeeId) {
        // Sprawdzenie czy pracownik istnieje
        if (!employeeRepository.existsById(employeeId)) {
            throw new EntityNotFoundException("Pracownik o ID " + employeeId + " nie został znaleziony");
        }

        List<ClothingAllowance> allowances = allowanceRepository.findByEmployeeId(employeeId);
        return ClothingAllowanceDTO.fromEntities(allowances);
    }

    @Override
    public List<ClothingAllowanceDTO> getAllowancesByEmployeeIdAndStatus(Long employeeId, AllowanceStatus status) {
        // Sprawdzenie czy pracownik istnieje
        if (!employeeRepository.existsById(employeeId)) {
            throw new EntityNotFoundException("Pracownik o ID " + employeeId + " nie został znaleziony");
        }

        List<ClothingAllowance> allowances = allowanceRepository.findByEmployeeIdAndStatus(employeeId, status);
        return ClothingAllowanceDTO.fromEntities(allowances);
    }

    @Override
    public List<ClothingAllowanceDTO> getAllowancesByPositionId(Long positionId) {
        // Sprawdzenie czy stanowisko istnieje
        if (!positionRepository.existsById(positionId)) {
            throw new EntityNotFoundException("Stanowisko o ID " + positionId + " nie zostało znalezione");
        }

        List<ClothingAllowance> allowances = allowanceRepository.findByPositionId(positionId);
        return ClothingAllowanceDTO.fromEntities(allowances);
    }

    @Override
    public List<ClothingAllowanceDTO> getAllowancesByStatus(AllowanceStatus status) {
        List<ClothingAllowance> allowances = allowanceRepository.findByStatus(status);
        return ClothingAllowanceDTO.fromEntities(allowances);
    }

    @Override
    public List<ClothingAllowanceDTO> getActiveAllowancesOnDate(LocalDate date) {
        List<ClothingAllowance> allowances = allowanceRepository.findActiveOnDate(date);
        return ClothingAllowanceDTO.fromEntities(allowances);
    }

    @Override
    public List<ClothingAllowanceDTO> getExpiredAllowances(LocalDate date) {
        List<ClothingAllowance> allowances = allowanceRepository.findExpiredAllowances(date, AllowanceStatus.ACTIVE);
        return ClothingAllowanceDTO.fromEntities(allowances);
    }

    @Override
    public ClothingAllowanceDTO updateAllowanceStatus(Long allowanceId, AllowanceStatus status) {
        ClothingAllowance allowance = allowanceRepository.findById(allowanceId)
                .orElseThrow(() -> new EntityNotFoundException("Przydział odzieżowy o ID " + allowanceId + " nie został znaleziony"));

        // Walidacja zmiany statusu
        if (allowance.getStatus() == AllowanceStatus.CANCELLED && status != AllowanceStatus.CANCELLED) {
            throw new BusinessException("Nie można zmienić statusu anulowanego przydziału");
        }

        allowance.setStatus(status);
        return ClothingAllowanceDTO.fromEntity(allowanceRepository.save(allowance));
    }

    @Override
    public ClothingAllowanceDTO updateAllowanceDates(Long allowanceId, LocalDate startDate, LocalDate endDate) {
        ClothingAllowance allowance = allowanceRepository.findById(allowanceId)
                .orElseThrow(() -> new EntityNotFoundException("Przydział odzieżowy o ID " + allowanceId + " nie został znaleziony"));

        // Walidacja statusu
        if (allowance.getStatus() == AllowanceStatus.CANCELLED) {
            throw new BusinessException("Nie można edytować anulowanego przydziału");
        }

        // Walidacja dat
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Data rozpoczęcia nie może być późniejsza niż data zakończenia");
        }

        allowance.setStartDate(startDate);
        allowance.setEndDate(endDate);
        return ClothingAllowanceDTO.fromEntity(allowanceRepository.save(allowance));
    }

    @Override
    public ClothingAllowanceDTO updateAllowanceNotes(Long allowanceId, String notes) {
        ClothingAllowance allowance = allowanceRepository.findById(allowanceId)
                .orElseThrow(() -> new EntityNotFoundException("Przydział odzieżowy o ID " + allowanceId + " nie został znaleziony"));

        allowance.setNotes(notes);
        return ClothingAllowanceDTO.fromEntity(allowanceRepository.save(allowance));
    }

    @Override
    public ClothingAllowanceDTO cancelAllowance(Long allowanceId, String reason) {
        ClothingAllowance allowance = allowanceRepository.findById(allowanceId)
                .orElseThrow(() -> new EntityNotFoundException("Przydział odzieżowy o ID " + allowanceId + " nie został znaleziony"));

        // Walidacja statusu
        if (allowance.getStatus() == AllowanceStatus.CANCELLED) {
            throw new BusinessException("Przydział jest już anulowany");
        }

        allowance.setStatus(AllowanceStatus.CANCELLED);
        allowance.setNotes(reason);
        return ClothingAllowanceDTO.fromEntity(allowanceRepository.save(allowance));
    }

    @Override
    @Transactional
    public int updateExpiredAllowances() {
        LocalDate today = LocalDate.now();
        List<ClothingAllowance> expiredAllowances = allowanceRepository.findExpiredAllowances(today, AllowanceStatus.ACTIVE);

        int count = 0;
        for (ClothingAllowance allowance : expiredAllowances) {
            allowance.setStatus(AllowanceStatus.EXPIRED);
            allowanceRepository.save(allowance);
            count++;
        }

        return count;
    }
}
