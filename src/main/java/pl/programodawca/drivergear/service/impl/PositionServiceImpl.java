package pl.programodawca.drivergear.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.programodawca.drivergear.dto.CreatePositionDTO;
import pl.programodawca.drivergear.dto.PositionDTO;
import pl.programodawca.drivergear.dto.UpdatePositionDTO;
import pl.programodawca.drivergear.exception.ResourceAlreadyExistsException;
import pl.programodawca.drivergear.exception.ResourceInUseException;
import pl.programodawca.drivergear.exception.ResourceNotFoundException;
import pl.programodawca.drivergear.model.Department;
import pl.programodawca.drivergear.model.Position;
import pl.programodawca.drivergear.repository.DepartmentRepository;
import pl.programodawca.drivergear.repository.PositionRepository;
import pl.programodawca.drivergear.service.PositionService;

import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {
    private final PositionRepository positionRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public List<PositionDTO> findAllPositions() {
        return positionRepository.findAll().stream()
                .map(PositionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<PositionDTO> findActivePositions() {
        return positionRepository.findByActive(true).stream()
                .map(PositionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<PositionDTO> findPositionsByDepartment(Long departmentId) {
        return positionRepository.findByDepartmentId(departmentId).stream()
                .map(PositionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<PositionDTO> findActivePositionsByDepartment(Long departmentId) {
        return positionRepository.findByDepartmentIdAndActive(departmentId, true).stream()
                .map(PositionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public PositionDTO findPositionById(Long id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stanowisko", "id", id));
        return PositionDTO.fromEntity(position);
    }

    @Override
    public PositionDTO createPosition(CreatePositionDTO createPositionDTO) {
        Department department = departmentRepository.findById(createPositionDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Dział", "id", createPositionDTO.getDepartmentId()));

        if (positionRepository.existsByNameAndDepartmentId(createPositionDTO.getName(), createPositionDTO.getDepartmentId())) {
            throw new ResourceAlreadyExistsException("Stanowisko o takiej nazwie już istnieje w tym dziale");
        }

        Position position = new Position();
        position.setName(createPositionDTO.getName());
        position.setDescription(createPositionDTO.getDescription());
        position.setDepartment(department);

        return PositionDTO.fromEntity(positionRepository.save(position));
    }

    @Override
    public PositionDTO updatePosition(Long id, UpdatePositionDTO updatePositionDTO) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stanowisko", "id", id));

        Department department = departmentRepository.findById(updatePositionDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Dział", "id", updatePositionDTO.getDepartmentId()));

        if (positionRepository.existsByNameAndDepartmentIdAndIdNot(
                updatePositionDTO.getName(), updatePositionDTO.getDepartmentId(), id)) {
            throw new ResourceAlreadyExistsException("Stanowisko o takiej nazwie już istnieje w tym dziale");
        }

        position.setName(updatePositionDTO.getName());
        position.setDescription(updatePositionDTO.getDescription());
        position.setActive(updatePositionDTO.isActive());
        position.setDepartment(department);

        return PositionDTO.fromEntity(positionRepository.save(position));
    }

    @Override
    public void deletePosition(Long id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stanowisko", "id", id));

        if (!position.getEmployees().isEmpty()) {
            throw new ResourceInUseException("Nie można usunąć stanowiska, które jest przypisane do pracowników");
        }

        positionRepository.delete(position);
    }
}





