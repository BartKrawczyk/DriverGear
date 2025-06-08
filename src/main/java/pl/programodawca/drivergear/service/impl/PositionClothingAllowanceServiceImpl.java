package pl.programodawca.drivergear.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.programodawca.drivergear.dto.PositionClothingAllowanceDTO;
import pl.programodawca.drivergear.dto.PositionClothingItemDTO;
import pl.programodawca.drivergear.exception.BusinessException;
import pl.programodawca.drivergear.exception.EntityNotFoundException;
import pl.programodawca.drivergear.model.ClothingType;
import pl.programodawca.drivergear.model.Position;
import pl.programodawca.drivergear.model.PositionClothingAllowance;
import pl.programodawca.drivergear.model.PositionClothingItem;
import pl.programodawca.drivergear.repository.ClothingAssignmentRepository;
import pl.programodawca.drivergear.repository.ClothingTypeRepository;
import pl.programodawca.drivergear.repository.DepartmentRepository;
import pl.programodawca.drivergear.repository.PositionClothingAllowanceRepository;
import pl.programodawca.drivergear.repository.PositionClothingItemRepository;
import pl.programodawca.drivergear.repository.PositionRepository;
import pl.programodawca.drivergear.service.PositionClothingAllowanceService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PositionClothingAllowanceServiceImpl implements PositionClothingAllowanceService {
    private final PositionClothingAllowanceRepository positionAllowanceRepository;
    private final PositionClothingItemRepository positionClothingItemRepository;
    private final PositionRepository positionRepository;
    private final ClothingTypeRepository clothingTypeRepository;
    private final DepartmentRepository departmentRepository;
    private final ClothingAssignmentRepository assignmentRepository;

    @Override
    public PositionClothingAllowanceDTO createPositionAllowance(Long departmentId, Long positionId, 
                                                              List<PositionClothingItemDTO> clothingItems, 
                                                              String notes) {
        // Validate department exists
        var department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Dział o ID " + departmentId + " nie został znaleziony"));

        // Validate position exists
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new EntityNotFoundException("Stanowisko o ID " + positionId + " nie zostało znalezione"));

        // Validate clothing items
        if (clothingItems == null || clothingItems.isEmpty()) {
            throw new BusinessException("Lista elementów odzieży nie może być pusta");
        }

        // Create new position clothing allowance
        PositionClothingAllowance allowance = new PositionClothingAllowance();
        allowance.setDepartment(department);
        allowance.setPosition(position);
        allowance.setActive(true);
        allowance.setNotes(notes);

        // Save the allowance first to get an ID
        PositionClothingAllowance savedAllowance = positionAllowanceRepository.save(allowance);

        // Create and add clothing items
        Set<PositionClothingItem> items = new HashSet<>();
        for (PositionClothingItemDTO itemDTO : clothingItems) {
            // Validate clothing type exists
            ClothingType clothingType = clothingTypeRepository.findById(itemDTO.getClothingTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("Typ odzieży o ID " + itemDTO.getClothingTypeId() + " nie został znaleziony"));

            // Check if this clothing type is already in the allowance
            boolean duplicateClothingType = items.stream()
                    .anyMatch(item -> item.getClothingType().getId().equals(itemDTO.getClothingTypeId()));

            if (duplicateClothingType) {
                throw new BusinessException("Przydział zawiera duplikat typu odzieży: " + clothingType.getName());
            }

            // Create new clothing item
            PositionClothingItem item = new PositionClothingItem();
            item.setPositionClothingAllowance(savedAllowance);
            item.setClothingType(clothingType);
            item.setQuantity(itemDTO.getQuantity());
            item.setValidityPeriod(itemDTO.getValidityPeriod());
            item.setMandatory(itemDTO.getMandatory());
            // barcode and compensationAmount are now derived from clothingType
            item.setActive(true);
            item.setNotes(itemDTO.getNotes());

            items.add(positionClothingItemRepository.save(item));
        }

        savedAllowance.setClothingItems(items);

        // Return DTO
        return PositionClothingAllowanceDTO.fromEntity(savedAllowance);
    }

    @Override
    public PositionClothingAllowanceDTO getPositionAllowanceById(Long allowanceId) {
        PositionClothingAllowance allowance = positionAllowanceRepository.findById(allowanceId)
                .orElseThrow(() -> new EntityNotFoundException("Przydział odzieżowy dla stanowiska o ID " + 
                        allowanceId + " nie został znaleziony"));
        return PositionClothingAllowanceDTO.fromEntity(allowance);
    }

    @Override
    public List<PositionClothingAllowanceDTO> getAllPositionAllowances() {
        List<PositionClothingAllowance> allowances = positionAllowanceRepository.findAll();
        return PositionClothingAllowanceDTO.fromEntities(allowances);
    }

    @Override
    public List<PositionClothingAllowanceDTO> getActivePositionAllowances() {
        List<PositionClothingAllowance> allowances = positionAllowanceRepository.findByActiveTrue();
        return PositionClothingAllowanceDTO.fromEntities(allowances);
    }

    @Override
    public List<PositionClothingAllowanceDTO> getPositionAllowancesByPositionId(Long positionId) {
        // Validate position exists
        if (!positionRepository.existsById(positionId)) {
            throw new EntityNotFoundException("Stanowisko o ID " + positionId + " nie zostało znalezione");
        }

        List<PositionClothingAllowance> allowances = positionAllowanceRepository.findByPositionId(positionId);
        return PositionClothingAllowanceDTO.fromEntities(allowances);
    }

    @Override
    public List<PositionClothingAllowanceDTO> getPositionAllowancesByDepartmentId(Long departmentId) {
        // Validate department exists
        if (!departmentRepository.existsById(departmentId)) {
            throw new EntityNotFoundException("Dział o ID " + departmentId + " nie został znaleziony");
        }

        List<PositionClothingAllowance> allowances = positionAllowanceRepository.findByDepartmentId(departmentId);
        return PositionClothingAllowanceDTO.fromEntities(allowances);
    }

    @Override
    public List<PositionClothingAllowanceDTO> getPositionAllowancesByClothingTypeId(Long clothingTypeId) {
        // Validate clothing type exists
        if (!clothingTypeRepository.existsById(clothingTypeId)) {
            throw new EntityNotFoundException("Typ odzieży o ID " + clothingTypeId + " nie został znaleziony");
        }

        // Find all clothing items with this clothing type
        List<PositionClothingItem> items = positionClothingItemRepository.findByClothingType_Id(clothingTypeId);

        // Get the unique allowances
        Set<PositionClothingAllowance> allowances = items.stream()
                .map(PositionClothingItem::getPositionClothingAllowance)
                .collect(Collectors.toSet());

        return PositionClothingAllowanceDTO.fromEntities(new ArrayList<>(allowances));
    }

    @Override
    public PositionClothingAllowanceDTO updatePositionAllowance(Long allowanceId, 
                                                              List<PositionClothingItemDTO> clothingItems, 
                                                              Boolean active, 
                                                              String notes) {
        // Validate allowance exists
        PositionClothingAllowance allowance = positionAllowanceRepository.findById(allowanceId)
                .orElseThrow(() -> new EntityNotFoundException("Przydział odzieżowy dla stanowiska o ID " + 
                        allowanceId + " nie został znaleziony"));

        // Update basic fields
        if (active != null) {
            allowance.setActive(active);
        }

        if (notes != null) {
            allowance.setNotes(notes);
        }

        // Update clothing items if provided
        if (clothingItems != null && !clothingItems.isEmpty()) {
            // Remove existing items
            List<PositionClothingItem> existingItems = positionClothingItemRepository.findByPositionClothingAllowanceId(allowanceId);
            for (PositionClothingItem item : existingItems) {
                positionClothingItemRepository.delete(item);
            }

            // Create new items
            Set<PositionClothingItem> newItems = new HashSet<>();
            for (PositionClothingItemDTO itemDTO : clothingItems) {
                // Validate clothing type exists
                ClothingType clothingType = clothingTypeRepository.findById(itemDTO.getClothingTypeId())
                        .orElseThrow(() -> new EntityNotFoundException("Typ odzieży o ID " + itemDTO.getClothingTypeId() + " nie został znaleziony"));

                // Check if this clothing type is already in the allowance
                boolean duplicateClothingType = newItems.stream()
                        .anyMatch(item -> item.getClothingType().getId().equals(itemDTO.getClothingTypeId()));

                if (duplicateClothingType) {
                    throw new BusinessException("Przydział zawiera duplikat typu odzieży: " + clothingType.getName());
                }

                // Create new clothing item
                PositionClothingItem item = new PositionClothingItem();
                item.setPositionClothingAllowance(allowance);
                item.setClothingType(clothingType);
                item.setQuantity(itemDTO.getQuantity());
                item.setValidityPeriod(itemDTO.getValidityPeriod());
                item.setMandatory(itemDTO.getMandatory());
                // barcode and compensationAmount are now derived from clothingType
                item.setActive(itemDTO.getActive() != null ? itemDTO.getActive() : true);
                item.setNotes(itemDTO.getNotes());

                newItems.add(positionClothingItemRepository.save(item));
            }

            allowance.setClothingItems(newItems);
        }

        // Save and return
        PositionClothingAllowance updatedAllowance = positionAllowanceRepository.save(allowance);
        return PositionClothingAllowanceDTO.fromEntity(updatedAllowance);
    }

    @Override
    public PositionClothingAllowanceDTO addClothingItemToAllowance(Long allowanceId, PositionClothingItemDTO clothingItem) {
        // Validate allowance exists
        PositionClothingAllowance allowance = positionAllowanceRepository.findById(allowanceId)
                .orElseThrow(() -> new EntityNotFoundException("Przydział odzieżowy dla stanowiska o ID " + 
                        allowanceId + " nie został znaleziony"));

        // Validate clothing type exists
        ClothingType clothingType = clothingTypeRepository.findById(clothingItem.getClothingTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Typ odzieży o ID " + clothingItem.getClothingTypeId() + " nie został znaleziony"));

        // Check if this clothing type is already in the allowance
        boolean clothingTypeExists = positionClothingItemRepository.existsByPositionClothingAllowanceIdAndClothingTypeId(
                allowanceId, clothingItem.getClothingTypeId());

        if (clothingTypeExists) {
            throw new BusinessException("Przydział już zawiera ten typ odzieży: " + clothingType.getName());
        }

        // Create new clothing item
        PositionClothingItem item = new PositionClothingItem();
        item.setPositionClothingAllowance(allowance);
        item.setClothingType(clothingType);
        item.setQuantity(clothingItem.getQuantity());
        item.setValidityPeriod(clothingItem.getValidityPeriod());
        item.setMandatory(clothingItem.getMandatory());
        // barcode and compensationAmount are now derived from clothingType
        item.setActive(clothingItem.getActive() != null ? clothingItem.getActive() : true);
        item.setNotes(clothingItem.getNotes());

        positionClothingItemRepository.save(item);

        // Refresh the allowance to get the updated items
        PositionClothingAllowance updatedAllowance = positionAllowanceRepository.findById(allowanceId).get();
        return PositionClothingAllowanceDTO.fromEntity(updatedAllowance);
    }

    @Override
    public PositionClothingItemDTO updateClothingItem(Long itemId, PositionClothingItemDTO clothingItem) {
        // Validate item exists
        PositionClothingItem item = positionClothingItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Element odzieży o ID " + itemId + " nie został znaleziony"));

        // Update fields
        if (clothingItem.getQuantity() != null) {
            item.setQuantity(clothingItem.getQuantity());
        }

        if (clothingItem.getValidityPeriod() != null) {
            item.setValidityPeriod(clothingItem.getValidityPeriod());
        }

        if (clothingItem.getMandatory() != null) {
            item.setMandatory(clothingItem.getMandatory());
        }

        // barcode and compensationAmount are now derived from clothingType and cannot be updated directly

        if (clothingItem.getActive() != null) {
            item.setActive(clothingItem.getActive());
        }

        if (clothingItem.getNotes() != null) {
            item.setNotes(clothingItem.getNotes());
        }

        // Save and return
        PositionClothingItem updatedItem = positionClothingItemRepository.save(item);
        return PositionClothingItemDTO.fromEntity(updatedItem);
    }

    @Override
    public void removeClothingItem(Long itemId) {
        // Validate item exists
        PositionClothingItem item = positionClothingItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Element odzieży o ID " + itemId + " nie został znaleziony"));

        // Delete the item
        positionClothingItemRepository.delete(item);
    }

    @Override
    public PositionClothingAllowanceDTO activatePositionAllowance(Long allowanceId) {
        // Validate allowance exists
        PositionClothingAllowance allowance = positionAllowanceRepository.findById(allowanceId)
                .orElseThrow(() -> new EntityNotFoundException("Przydział odzieżowy dla stanowiska o ID " + 
                        allowanceId + " nie został znaleziony"));

        allowance.setActive(true);
        PositionClothingAllowance updatedAllowance = positionAllowanceRepository.save(allowance);
        return PositionClothingAllowanceDTO.fromEntity(updatedAllowance);
    }

    @Override
    public PositionClothingAllowanceDTO deactivatePositionAllowance(Long allowanceId) {
        // Validate allowance exists
        PositionClothingAllowance allowance = positionAllowanceRepository.findById(allowanceId)
                .orElseThrow(() -> new EntityNotFoundException("Przydział odzieżowy dla stanowiska o ID " + 
                        allowanceId + " nie został znaleziony"));

        allowance.setActive(false);
        PositionClothingAllowance updatedAllowance = positionAllowanceRepository.save(allowance);
        return PositionClothingAllowanceDTO.fromEntity(updatedAllowance);
    }

    @Override
    public void deletePositionAllowance(Long allowanceId) {
        // Validate allowance exists
        PositionClothingAllowance allowance = positionAllowanceRepository.findById(allowanceId)
                .orElseThrow(() -> new EntityNotFoundException("Przydział odzieżowy dla stanowiska o ID " + 
                        allowanceId + " nie został znaleziony"));

        // Check if the allowance is in use
        if (isPositionAllowanceInUse(allowanceId)) {
            throw new BusinessException("Nie można usunąć przydziału odzieżowego, który jest używany");
        }

        // Delete all clothing items first
        List<PositionClothingItem> items = positionClothingItemRepository.findByPositionClothingAllowanceId(allowanceId);
        for (PositionClothingItem item : items) {
            positionClothingItemRepository.delete(item);
        }

        // Delete the allowance
        positionAllowanceRepository.delete(allowance);
    }

    @Override
    public boolean isPositionAllowanceInUse(Long allowanceId) {
        // Check if there are any assignments using this allowance
        return !assignmentRepository.findByPositionClothingAllowanceId(allowanceId).isEmpty();
    }
}
