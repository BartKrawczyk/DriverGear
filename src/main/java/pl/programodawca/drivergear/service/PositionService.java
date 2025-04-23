package pl.programodawca.drivergear.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.programodawca.drivergear.dto.CreatePositionDTO;
import pl.programodawca.drivergear.dto.PositionDTO;
import pl.programodawca.drivergear.dto.UpdatePositionDTO;

import java.util.List;

public interface PositionService {
    List<PositionDTO> findAllPositions();
    List<PositionDTO> findActivePositions();
    List<PositionDTO> findPositionsByDepartment(Long departmentId);
    List<PositionDTO> findActivePositionsByDepartment(Long departmentId);
    PositionDTO findPositionById(Long id);
    PositionDTO findPositionByCode(String code);
    PositionDTO createPosition(CreatePositionDTO createPositionDTO);
    PositionDTO updatePosition(Long id, UpdatePositionDTO updatePositionDTO);
    void deletePosition(Long id);

    // Metody pomocnicze do walidacji
    boolean isCodeUnique(String code);
    boolean isCodeUnique(String code, Long excludeId);
}









