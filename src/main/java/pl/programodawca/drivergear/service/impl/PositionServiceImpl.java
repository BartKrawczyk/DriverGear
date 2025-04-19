package pl.programodawca.drivergear.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.programodawca.drivergear.dto.CreatePositionDTO;
import pl.programodawca.drivergear.dto.UpdatePositionDTO;
import pl.programodawca.drivergear.dto.PositionDTO;
import pl.programodawca.drivergear.exception.DepartmentNotFoundException;
import pl.programodawca.drivergear.exception.PositionAlreadyExistsException;
import pl.programodawca.drivergear.exception.PositionNotFoundException;
import pl.programodawca.drivergear.model.Department;
import pl.programodawca.drivergear.model.Position;
import pl.programodawca.drivergear.repository.DepartmentRepository;
import pl.programodawca.drivergear.repository.PositionRepository;
import pl.programodawca.drivergear.service.PositionService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class PositionServiceImpl implements PositionService {
    private final PositionRepository positionRepository;
    private final DepartmentRepository departmentRepository;

    public PositionServiceImpl(PositionRepository positionRepository, DepartmentRepository departmentRepository) {
        this.positionRepository = positionRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PositionDTO> getAllPositions() {
        log.debug("Pobieranie wszystkich stanowisk");
        return positionRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public PositionDTO createPosition(CreatePositionDTO createPositionDTO) {
        log.info("Próba utworzenia nowego stanowiska: {} w dziale ID: {}", createPositionDTO.getName(), createPositionDTO.getDepartmentId());

        Department department = getDepartment(createPositionDTO.getDepartmentId());
        validatePositionUniqueness(null, createPositionDTO.getName(), createPositionDTO.getCode(), createPositionDTO.getDepartmentId());

        Position position = new Position();
        position.setName(createPositionDTO.getName());
        position.setCode(createPositionDTO.getCode().toUpperCase());
        position.setDescription(createPositionDTO.getDescription());
        position.setDepartment(department);

        Position savedPosition = positionRepository.save(position);
        log.info("Utworzono nowe stanowisko: {} (ID: {})", savedPosition.getName(), savedPosition.getId());

        return mapToDTO(savedPosition);
    }

    @Override
    public PositionDTO updatePosition(Long id, UpdatePositionDTO updatePositionDTO) {
        log.info("Próba aktualizacji stanowiska o ID: {}", id);

        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new PositionNotFoundException("Nie znaleziono stanowiska o id: " + id));

        Department department = getDepartment(updatePositionDTO.getDepartmentId());
        validatePositionUniqueness(id, updatePositionDTO.getName(), updatePositionDTO.getCode(), updatePositionDTO.getDepartmentId());

        position.setName(updatePositionDTO.getName());
        position.setCode(updatePositionDTO.getCode().toUpperCase());
        position.setDescription(updatePositionDTO.getDescription());
        position.setDepartment(department);

        Position updatedPosition = positionRepository.save(position);
        log.info("Zaktualizowano stanowisko o ID: {}", id);

        return mapToDTO(updatedPosition);
    }

    @Override
    public PositionDTO getPositionById(Long id) {
        return null;
    }

    @Override
    public PositionDTO findById(Long id) {
        log.debug("Wyszukiwanie stanowiska o ID: {}", id);
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new PositionNotFoundException("Nie znaleziono stanowiska o id: " + id));
        return mapToDTO(position);
    }

    @Override
    public List<PositionDTO> getPositionsByDepartmentId(Long departmentId) {
        return List.of();
    }

    @Override
    @Transactional
    public void deletePosition(Long id) {
        log.debug("Próba usunięcia stanowiska o ID: {}", id);

        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono stanowiska o ID: " + id));

        if (!position.getEmployees().isEmpty()) {
            throw new IllegalStateException(String.format(
                    "Nie można usunąć stanowiska '%s', ponieważ jest przypisane do pracowników",
                    position.getName()
            ));
        }

        try {
            positionRepository.delete(position);
            log.debug("Stanowisko '{}' (ID: {}) zostało usunięte", position.getName(), id);
        } catch (Exception e) {
            log.error("Błąd podczas usuwania stanowiska '{}' (ID: {})", position.getName(), id, e);
            throw new RuntimeException("Wystąpił błąd podczas usuwania stanowiska: " + e.getMessage(), e);
        }
    }




    private Department getDepartment(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException("Nie znaleziono działu o id: " + departmentId));
    }

    private void validatePositionUniqueness(Long positionId, String name, String code, Long departmentId) {
        // Dla nowego stanowiska (positionId == null)
        if (positionId == null) {
            validateNewPosition(name, code, departmentId);
            return;
        }

        // Dla istniejącego stanowiska
        Position existingPosition = positionRepository.findById(positionId)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono stanowiska o ID: " + positionId));

        // Sprawdzamy nazwę tylko jeśli się zmieniła
        if (!existingPosition.getName().equalsIgnoreCase(name)) {
            if (positionRepository.existsByNameAndDepartmentIdAndIdNot(name, departmentId, positionId)) {
                throw new PositionAlreadyExistsException("Stanowisko o nazwie '" + name + "' już istnieje w tym dziale");
            }
        }

        // Sprawdzamy kod tylko jeśli się zmienił
        if (!existingPosition.getCode().equalsIgnoreCase(code)) {
            if (positionRepository.existsByCodeAndDepartmentIdAndIdNot(code, departmentId, positionId)) {
                throw new PositionAlreadyExistsException("Stanowisko o kodzie '" + code + "' już istnieje w tym dziale");
            }
        }
    }

    private void validateNewPosition(String name, String code, Long departmentId) {
        if (positionRepository.existsByNameAndDepartmentId(name, departmentId)) {
            throw new PositionAlreadyExistsException("Stanowisko o nazwie '" + name + "' już istnieje w tym dziale");
        }

        if (positionRepository.existsByCodeAndDepartmentId(code, departmentId)) {
            throw new PositionAlreadyExistsException("Stanowisko o kodzie '" + code + "' już istnieje w tym dziale");
        }
    }



    private PositionDTO mapToDTO(Position position) {
        return PositionDTO.builder()
                .id(position.getId())
                .name(position.getName())
                .code(position.getCode())
                .description(position.getDescription())
                .departmentId(position.getDepartment().getId())
                .departmentName(position.getDepartment().getName())
                .build();
    }
}
