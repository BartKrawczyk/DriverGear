package pl.programodawca.drivergear.service;

import pl.programodawca.drivergear.dto.EmployeeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    Page<EmployeeDTO> findAll(Pageable pageable);
    Page<EmployeeDTO> findAllByActive(boolean active, Pageable pageable);  // nowa metoda
    Page<EmployeeDTO> findAllByPosition(Long positionId, Pageable pageable);
    Page<EmployeeDTO> findAllByDepartment(Long departmentId, Pageable pageable);

    /**
     * Find employees by various filter criteria.
     * 
     * @param firstName Optional first name filter (partial match)
     * @param lastName Optional last name filter (partial match)
     * @param employeeNumber Optional employee number filter (partial match)
     * @param departmentId Optional department ID filter (exact match)
     * @param positionId Optional position ID filter (exact match)
     * @param pageable Pagination information
     * @return A page of employee DTOs matching the filter criteria
     */
    Page<EmployeeDTO> findByFilters(String firstName, String lastName, String employeeNumber, 
                                   Long departmentId, Long positionId, Pageable pageable);

    EmployeeDTO findById(Long id);
    EmployeeDTO create(EmployeeDTO employeeDTO);
    EmployeeDTO update(Long id, EmployeeDTO employeeDTO);
    void delete(Long id);
    boolean existsByPosition(Long positionId);
    long countByPosition(Long positionId);

    /**
     * Ensures that an employee has all the clothing assignments they should have based on their position.
     * This method checks for any missing assignments and creates them if necessary.
     * 
     * @param employeeId The ID of the employee to check
     * @return The number of new assignments created
     */
    int ensureClothingAssignmentsForEmployee(Long employeeId);
}
