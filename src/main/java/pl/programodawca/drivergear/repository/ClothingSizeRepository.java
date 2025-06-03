package pl.programodawca.drivergear.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.programodawca.drivergear.model.ClothingSize;
import pl.programodawca.drivergear.model.ClothingType;
import pl.programodawca.drivergear.model.Employee;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClothingSizeRepository extends JpaRepository<ClothingSize, Long> {
    List<ClothingSize> findByEmployee(Employee employee);
    List<ClothingSize> findByEmployeeId(Long employeeId);
    List<ClothingSize> findByClothingType(ClothingType clothingType);
    List<ClothingSize> findByClothingTypeId(Long clothingTypeId);
    Optional<ClothingSize> findByEmployeeAndClothingType(Employee employee, ClothingType clothingType);
    Optional<ClothingSize> findByEmployeeIdAndClothingTypeId(Long employeeId, Long clothingTypeId);
    List<ClothingSize> findByActive(boolean active);
}