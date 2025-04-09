package pl.programodawca.drivergear.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.programodawca.drivergear.model.Clothing;

import java.util.Optional;

public interface ClothingRepository extends JpaRepository<Clothing, Long> {

    // Wyszukiwanie ubrania po kodzie kreskowym
    Optional<Clothing> findByBarcode(String barcode);
}

