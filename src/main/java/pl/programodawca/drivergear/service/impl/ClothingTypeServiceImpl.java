package pl.programodawca.drivergear.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.programodawca.drivergear.dto.ClothingTypeDTO;
import pl.programodawca.drivergear.exception.ResourceAlreadyExistsException;
import pl.programodawca.drivergear.exception.ResourceInUseException;
import pl.programodawca.drivergear.exception.ResourceNotFoundException;
import pl.programodawca.drivergear.model.ClothingType;
import pl.programodawca.drivergear.repository.ClothingTypeRepository;
import pl.programodawca.drivergear.service.ClothingTypeService;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ClothingTypeServiceImpl implements ClothingTypeService {
    private final ClothingTypeRepository clothingTypeRepository;

    @Override
    public List<ClothingTypeDTO> findAllClothingTypes() {
        List<ClothingType> clothingTypes = clothingTypeRepository.findAll();
        return ClothingTypeDTO.fromEntities(clothingTypes);
    }

    @Override
    public List<ClothingTypeDTO> findActiveClothingTypes() {
        List<ClothingType> clothingTypes = clothingTypeRepository.findByActiveTrue();
        return ClothingTypeDTO.fromEntities(clothingTypes);
    }

    @Override
    public ClothingTypeDTO findById(Long id) throws ResourceNotFoundException {
        ClothingType clothingType = clothingTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Typ odzieży", "id", id));
        return ClothingTypeDTO.fromEntity(clothingType);
    }

    @Override
    public ClothingTypeDTO createClothingType(ClothingTypeDTO clothingTypeDTO) {
        // Check if a clothing type with the same name already exists
        if (clothingTypeRepository.existsByName(clothingTypeDTO.getName())) {
            throw new ResourceAlreadyExistsException("Typ odzieży o nazwie '" + clothingTypeDTO.getName() + "' już istnieje");
        }

        // Check if a clothing type with the same barcode already exists (if barcode is provided)
        if (clothingTypeDTO.getBarcode() != null && !clothingTypeDTO.getBarcode().isEmpty() && 
                clothingTypeRepository.existsByBarcode(clothingTypeDTO.getBarcode())) {
            throw new ResourceAlreadyExistsException("Typ odzieży o kodzie kreskowym '" + clothingTypeDTO.getBarcode() + "' już istnieje");
        }

        ClothingType clothingType = clothingTypeDTO.toEntity();
        ClothingType savedClothingType = clothingTypeRepository.save(clothingType);
        return ClothingTypeDTO.fromEntity(savedClothingType);
    }

    @Override
    public ClothingTypeDTO updateClothingType(Long id, ClothingTypeDTO clothingTypeDTO) throws ResourceNotFoundException {
        // Check if the clothing type exists
        ClothingType clothingType = clothingTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Typ odzieży", "id", id));

        // Check if a clothing type with the same name already exists (excluding this one)
        if (!clothingType.getName().equals(clothingTypeDTO.getName()) && 
                clothingTypeRepository.existsByNameAndIdNot(clothingTypeDTO.getName(), id)) {
            throw new ResourceAlreadyExistsException("Typ odzieży o nazwie '" + clothingTypeDTO.getName() + "' już istnieje");
        }

        // Check if a clothing type with the same barcode already exists (excluding this one)
        String existingBarcode = clothingType.getBarcode();
        String newBarcode = clothingTypeDTO.getBarcode();
        if (newBarcode != null && !newBarcode.isEmpty() && 
                (existingBarcode == null || !existingBarcode.equals(newBarcode)) && 
                clothingTypeRepository.existsByBarcodeAndIdNot(newBarcode, id)) {
            throw new ResourceAlreadyExistsException("Typ odzieży o kodzie kreskowym '" + newBarcode + "' już istnieje");
        }

        // Update the clothing type
        clothingTypeDTO.updateEntity(clothingType);
        ClothingType updatedClothingType = clothingTypeRepository.save(clothingType);
        return ClothingTypeDTO.fromEntity(updatedClothingType);
    }

    @Override
    public void deleteClothingType(Long id) throws ResourceNotFoundException {
        // Check if the clothing type exists
        ClothingType clothingType = clothingTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Typ odzieży", "id", id));

        // Check if the clothing type is in use
        if (isClothingTypeInUse(id)) {
            throw new ResourceInUseException("Nie można usunąć typu odzieży, który jest używany");
        }

        clothingTypeRepository.delete(clothingType);
    }

    @Override
    public boolean isClothingTypeInUse(Long id) {
        ClothingType clothingType = clothingTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Typ odzieży", "id", id));

        // Check if the clothing type is used in clothing items
        return !clothingType.getClothingItems().isEmpty();
    }
}
