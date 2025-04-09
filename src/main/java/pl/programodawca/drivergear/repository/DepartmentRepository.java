package pl.programodawca.drivergear.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.programodawca.drivergear.model.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // Przykład dodatkowej metody: Znajdź Department po nazwie
    Department findByName(String name);
}
