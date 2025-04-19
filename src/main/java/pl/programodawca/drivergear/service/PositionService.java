package pl.programodawca.drivergear.service;

import pl.programodawca.drivergear.dto.CreatePositionDTO;
import pl.programodawca.drivergear.dto.UpdatePositionDTO;
import pl.programodawca.drivergear.dto.PositionDTO;
import pl.programodawca.drivergear.model.Position;

import java.util.List;

public interface PositionService {
    PositionDTO createPosition(CreatePositionDTO createPositionDTO);
    PositionDTO updatePosition(Long id, UpdatePositionDTO updatePositionDTO);
    PositionDTO getPositionById(Long id);
    List<PositionDTO> getAllPositions();
    List<PositionDTO> getPositionsByDepartmentId(Long departmentId);
    void deletePosition(Long id);
    PositionDTO findById(Long id);
}


