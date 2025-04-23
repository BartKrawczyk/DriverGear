package pl.programodawca.drivergear.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.programodawca.drivergear.dto.CreateDepartmentDTO;
import pl.programodawca.drivergear.dto.DepartmentDTO;
import pl.programodawca.drivergear.dto.UpdateDepartmentDTO;
import pl.programodawca.drivergear.model.Department;
import pl.programodawca.drivergear.repository.DepartmentRepository;
import pl.programodawca.drivergear.service.DepartmentService;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public List<DepartmentDTO> getAllActiveDepartments() {
        return departmentRepository.findAllActive().stream()
                .map(DepartmentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)  // zmiana z findActiveById
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono działu o ID: " + id));
        return DepartmentDTO.fromEntity(department);
    }

    @Override
    @Transactional
    public DepartmentDTO createDepartment(CreateDepartmentDTO createDTO) {
        if (departmentRepository.existsByCode(createDTO.getCode())) {
            throw new ValidationException("Dział o kodzie " + createDTO.getCode() + " już istnieje");
        }

        Department department = createDTO.toEntity();
        department = departmentRepository.save(department);
        return DepartmentDTO.fromEntity(department);
    }

    @Override
    @Transactional
    public DepartmentDTO updateDepartment(Long id, UpdateDepartmentDTO updateDTO) {
        if (!id.equals(updateDTO.getId())) {
            throw new ValidationException("ID w ścieżce nie zgadza się z ID w obiekcie");
        }

        Department department = departmentRepository.findById(id)  // zmiana z findActiveById
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono działu o ID: " + id));

        updateDTO.updateEntity(department);
        department = departmentRepository.save(department);
        return DepartmentDTO.fromEntity(department);
    }


    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono działu o ID: " + id));

        if (departmentRepository.hasActiveEmployees(id)) {
            throw new IllegalStateException("Nie można usunąć działu, który ma aktywnych pracowników");
        }

        departmentRepository.softDelete(id);
    }

    @Override
    public List<DepartmentDTO> findAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(DepartmentDTO::fromEntity)
                .collect(Collectors.toList());
    }

}








