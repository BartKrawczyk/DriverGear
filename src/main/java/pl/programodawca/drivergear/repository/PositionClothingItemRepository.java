package pl.programodawca.drivergear.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.programodawca.drivergear.model.PositionClothingItem;

import java.util.List;

@Repository
public interface PositionClothingItemRepository extends JpaRepository<PositionClothingItem, Long> {
    List<PositionClothingItem> findByPositionClothingAllowanceId(Long positionClothingAllowanceId);
    
    List<PositionClothingItem> findByClothingTypeId(Long clothingTypeId);
    
    List<PositionClothingItem> findByActiveTrue();
    
    boolean existsByPositionClothingAllowanceIdAndClothingTypeId(Long positionClothingAllowanceId, Long clothingTypeId);
}