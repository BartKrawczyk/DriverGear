package pl.programodawca.drivergear.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.programodawca.drivergear.dto.CreateDepartmentDTO;
import pl.programodawca.drivergear.dto.DepartmentDTO;
import pl.programodawca.drivergear.dto.UpdateDepartmentDTO;
import pl.programodawca.drivergear.exception.DepartmentAlreadyExistsException;
import pl.programodawca.drivergear.exception.DepartmentNotFoundException;
import pl.programodawca.drivergear.model.Department;
import pl.programodawca.drivergear.repository.DepartmentRepository;
import pl.programodawca.drivergear.service.DepartmentService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public DepartmentDTO createDepartment(CreateDepartmentDTO createDepartmentDTO) {
        log.info("Próba utworzenia nowego działu: {}", createDepartmentDTO.getName());

        if (departmentRepository.existsByName(createDepartmentDTO.getName())) {
            log.warn("Próba utworzenia działu o istniejącej nazwie: {}", createDepartmentDTO.getName());
            throw new DepartmentAlreadyExistsException("Dział o nazwie " + createDepartmentDTO.getName() + " już istnieje");
        }
        if (departmentRepository.existsByCode(createDepartmentDTO.getCode())) {
            log.warn("Próba utworzenia działu o istniejącym kodzie: {}", createDepartmentDTO.getCode());
            throw new DepartmentAlreadyExistsException("Dział o kodzie " + createDepartmentDTO.getCode() + " już istnieje");
        }

        Department department = new Department();
        department.setName(createDepartmentDTO.getName());
        department.setCode(createDepartmentDTO.getCode().toUpperCase());
        department.setDescription(createDepartmentDTO.getDescription());

        Department savedDepartment = departmentRepository.save(department);
        log.info("Utworzono nowy dział: {} (ID: {})", savedDepartment.getName(), savedDepartment.getId());
        return mapToDTO(savedDepartment);
    }

    @Override
    public DepartmentDTO updateDepartment(Long id, UpdateDepartmentDTO updateDepartmentDTO) {
        log.info("Próba aktualizacji działu o ID: {}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException("Nie znaleziono działu o id: " + id));

        if (!department.getName().equals(updateDepartmentDTO.getName()) && departmentRepository.existsByName(updateDepartmentDTO.getName())) {
            log.warn("Próba aktualizacji działu na istniejącą nazwę: {}", updateDepartmentDTO.getName());
            throw new DepartmentAlreadyExistsException("Dział o nazwie " + updateDepartmentDTO.getName() + " już istnieje");
        }
        if (!department.getCode().equals(updateDepartmentDTO.getCode()) && departmentRepository.existsByCode(updateDepartmentDTO.getCode())) {
            log.warn("Próba aktualizacji działu na istniejący kod: {}", updateDepartmentDTO.getCode());
            throw new DepartmentAlreadyExistsException("Dział o kodzie " + updateDepartmentDTO.getCode() + " już istnieje");
        }

        department.setName(updateDepartmentDTO.getName());
        department.setCode(updateDepartmentDTO.getCode().toUpperCase());
        department.setDescription(updateDepartmentDTO.getDescription());

        Department updatedDepartment = departmentRepository.save(department);
        log.info("Zaktualizowano dział: {} (ID: {})", updatedDepartment.getName(), updatedDepartment.getId());
        return mapToDTO(updatedDepartment);
    }

    @Override
    public DepartmentDTO getDepartmentById(Long id) {
        log.debug("Pobieranie działu o ID: {}", id);
        return departmentRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new DepartmentNotFoundException("Nie znaleziono działu o id: " + id));
    }

    @Override
    public List<DepartmentDTO> getAllDepartments() {
        log.debug("Pobieranie wszystkich działów");
        return departmentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentDTO findById(Long id) {
        return getDepartmentById(id);
    }

    @Override
    public void deleteDepartment(Long id) {
        log.info("Próba usunięcia działu o ID: {}", id);
        if (!departmentRepository.existsById(id)) {
            throw new DepartmentNotFoundException("Nie znaleziono działu o id: " + id);
        }
        departmentRepository.deleteById(id);
        log.info("Usunięto dział o ID: {}", id);
    }

    @Override
    public List<Department> findAll() {
        return List.of();
    }

    private DepartmentDTO mapToDTO(Department department) {
        return DepartmentDTO.builder()
                .id(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .description(department.getDescription())
                .build();
    }
}


