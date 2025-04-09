package pl.programodawca.drivergear.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.programodawca.drivergear.model.Storage;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {

    // Znajdź wpis dotyczący danego ubrania
    Storage findByClothingId(Long clothingId);

    // Usuń wpis według identyfikatora ubrania
    void deleteByClothingId(Long clothingId);
}

