package pl.programodawca.drivergear.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.programodawca.drivergear.model.ClothingType;
import pl.programodawca.drivergear.model.Employee;
import pl.programodawca.drivergear.model.TransactionType;
import pl.programodawca.drivergear.model.WarehouseTransaction;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WarehouseTransactionRepository extends JpaRepository<WarehouseTransaction, Long> {
    List<WarehouseTransaction> findByEmployee(Employee employee);
    List<WarehouseTransaction> findByEmployeeId(Long employeeId);
    List<WarehouseTransaction> findByClothingType(ClothingType clothingType);
    List<WarehouseTransaction> findByClothingTypeId(Long clothingTypeId);
    List<WarehouseTransaction> findByTransactionType(TransactionType transactionType);
    List<WarehouseTransaction> findByTransactionDate(LocalDate transactionDate);
    List<WarehouseTransaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);
    List<WarehouseTransaction> findByEmployeeIdAndTransactionType(Long employeeId, TransactionType transactionType);
    List<WarehouseTransaction> findByEmployeeIdAndTransactionDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);
}