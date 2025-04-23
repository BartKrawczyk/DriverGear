package pl.programodawca.drivergear.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.programodawca.drivergear.dto.EmployeeDTO;
import pl.programodawca.drivergear.exception.ResourceNotFoundException;
import pl.programodawca.drivergear.model.Employee;
import pl.programodawca.drivergear.model.Position;
import pl.programodawca.drivergear.repository.EmployeeRepository;
import pl.programodawca.drivergear.repository.PositionRepository;
import pl.programodawca.drivergear.service.EmployeeService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;

    @Override
    public Page<EmployeeDTO> findAll(Pageable pageable) {
        return employeeRepository.findAll(pageable)
                .map(EmployeeDTO::fromEntity);
    }

    @Override
    public Page<EmployeeDTO> findAllByActive(boolean active, Pageable pageable) {
        return employeeRepository.findAllByActive(active, pageable)
                .map(EmployeeDTO::fromEntity);
    }


    @Override
    public Page<EmployeeDTO> findAllByPosition(Long positionId, Pageable pageable) {
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new ResourceNotFoundException("Stanowisko", "id", positionId));
        return employeeRepository.findAllByPosition(position, pageable)
                .map(EmployeeDTO::fromEntity);
    }

    @Override
    public Page<EmployeeDTO> findAllByDepartment(Long departmentId, Pageable pageable) {
        return employeeRepository.findAllByPosition_Department_Id(departmentId, pageable)
                .map(EmployeeDTO::fromEntity);
    }

    @Override
    public EmployeeDTO findById(Long id) {
        return employeeRepository.findById(id)
                .map(EmployeeDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Pracownik", "id", id));
    }

    @Override
    public EmployeeDTO create(EmployeeDTO employeeDTO) {
        Position position = positionRepository.findById(employeeDTO.getPositionId())
                .orElseThrow(() -> new ResourceNotFoundException("Stanowisko", "id", employeeDTO.getPositionId()));

        Employee employee = employeeDTO.toEntity();
        employee.setPosition(position);
        Employee savedEmployee = employeeRepository.save(employee);
        return EmployeeDTO.fromEntity(savedEmployee);
    }

    @Override
    public EmployeeDTO update(Long id, EmployeeDTO employeeDTO) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pracownik", "id", id));

        Position position = positionRepository.findById(employeeDTO.getPositionId())
                .orElseThrow(() -> new ResourceNotFoundException("Stanowisko", "id", employeeDTO.getPositionId()));

        employeeDTO.updateEntity(employee);
        employee.setPosition(position);
        Employee updatedEmployee = employeeRepository.save(employee);
        return EmployeeDTO.fromEntity(updatedEmployee);
    }

    @Override
    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pracownik", "id", id);
        }
        employeeRepository.deleteById(id);
    }

    @Override
    public boolean existsByPosition(Long positionId) {
        return employeeRepository.existsByPosition_Id(positionId);
    }

    @Override
    public long countByPosition(Long positionId) {
        return employeeRepository.countByPosition_Id(positionId);
    }
}

