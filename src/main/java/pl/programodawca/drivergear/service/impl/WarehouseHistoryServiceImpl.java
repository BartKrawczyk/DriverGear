package pl.programodawca.drivergear.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.programodawca.drivergear.dto.ClothingAssignmentDTO;
import pl.programodawca.drivergear.dto.ClothingCompensationDTO;
import pl.programodawca.drivergear.model.ClothingAssignment;
import pl.programodawca.drivergear.model.ClothingCompensation;
import pl.programodawca.drivergear.repository.ClothingAssignmentRepository;
import pl.programodawca.drivergear.repository.ClothingCompensationRepository;
import pl.programodawca.drivergear.service.WarehouseHistoryService;

import java.time.LocalDate;

/**
 * Implementation of the WarehouseHistoryService interface.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WarehouseHistoryServiceImpl implements WarehouseHistoryService {

    private final ClothingAssignmentRepository clothingAssignmentRepository;
    private final ClothingCompensationRepository clothingCompensationRepository;

    @Override
    public Page<ClothingAssignmentDTO> getIssuedClothingHistory(
            String firstName,
            String lastName,
            String employeeNumber,
            Long clothingTypeId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {
        
        Page<ClothingAssignment> assignmentsPage = clothingAssignmentRepository.findIssuedClothingHistory(
                firstName,
                lastName,
                employeeNumber,
                clothingTypeId,
                startDate,
                endDate,
                pageable
        );

        return assignmentsPage.map(a -> ClothingAssignmentDTO.fromEntity(a, clothingCompensationRepository));
    }

    @Override
    public Page<ClothingCompensationDTO> getPaidCompensationHistory(
            String firstName,
            String lastName,
            String employeeNumber,
            Long clothingTypeId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {
        
        Page<ClothingCompensation> compensationsPage = clothingCompensationRepository.findPaidCompensationHistory(
                firstName,
                lastName,
                employeeNumber,
                clothingTypeId,
                startDate,
                endDate,
                pageable
        );
        
        return compensationsPage.map(ClothingCompensationDTO::fromEntity);
    }
}