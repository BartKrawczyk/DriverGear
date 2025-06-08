package pl.programodawca.drivergear.service;

import pl.programodawca.drivergear.dto.CreateDepartmentDTO;
import pl.programodawca.drivergear.dto.DepartmentDTO;
import pl.programodawca.drivergear.dto.UpdateDepartmentDTO;
import java.util.List;

public interface DepartmentService {
    /**
     * Pobiera listę wszystkich aktywnych działów wraz z ich stanowiskami
     */
    List<DepartmentDTO> getAllActiveDepartments();

    /**
     * Pobiera dział po ID wraz ze stanowiskami
     * @throws javax.persistence.EntityNotFoundException jeśli dział nie istnieje
     */
    DepartmentDTO getDepartmentById(Long id);

    /**
     * Tworzy nowy dział
     * @throws javax.validation.ValidationException jeśli kod działu już istnieje
     */
    DepartmentDTO createDepartment(CreateDepartmentDTO createDTO);

    /**
     * Aktualizuje istniejący dział
     * @throws javax.persistence.EntityNotFoundException jeśli dział nie istnieje
     */
    DepartmentDTO updateDepartment(Long id, UpdateDepartmentDTO updateDTO);

    /**
     * Usuwa (dezaktywuje) dział
     * @throws javax.persistence.EntityNotFoundException jeśli dział nie istnieje
     * @throws IllegalStateException jeśli dział ma aktywnych pracowników
     */
    void deleteDepartment(Long id);

    List<DepartmentDTO> findAllDepartments();

}




