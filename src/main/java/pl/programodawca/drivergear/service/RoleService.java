package pl.programodawca.drivergear.service;

import pl.programodawca.drivergear.model.Role;

import java.util.List;

public interface RoleService {

    /**
     * Znajdź rolę po jej identyfikatorze
     *
     * @param id identyfikator roli
     * @return obiekt Role
     * @throws RuntimeException, gdy rola nie istnieje
     */
    Role findById(Long id);

    /**
     * Znajdź rolę po jej nazwie
     *
     * @param name nazwa roli
     * @return obiekt Role
     * @throws RuntimeException, gdy rola nie istnieje
     */
    Role findByName(String name);

    /**
     * Utwórz nową rolę lub zaktualizuj istniejącą
     *
     * @param role rola do zapisania
     * @return zapisany obiekt Role
     */
    Role save(Role role);

    /**
     * Pobierz wszystkie role
     *
     * @return lista ról
     */
    List<Role> findAll();
}