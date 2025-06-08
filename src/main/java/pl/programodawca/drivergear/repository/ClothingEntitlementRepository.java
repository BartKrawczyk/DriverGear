package pl.programodawca.drivergear.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.programodawca.drivergear.model.ClothingEntitlement;
import pl.programodawca.drivergear.model.Employee;
import pl.programodawca.drivergear.model.Clothing;

import java.util.List;

@Repository
public interface ClothingEntitlementRepository extends JpaRepository<ClothingEntitlement, Long> {

    // Znajdź wszystkie przydziały dla konkretnego pracownika
    List<ClothingEntitlement> findByEmployee(Employee employee);

    // Znajdź wszystkie przydziały dla pracownika po statusie niewykorzystania
    List<ClothingEntitlement> findByEmployeeAndUsedQuantityLessThan(Employee employee, int usedQuantity);

    // Znajdź przydział pracownika dla konkretnego ubrania
    List<ClothingEntitlement> findByEmployeeAndClothing(Employee employee, Clothing clothing);
}
