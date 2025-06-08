package pl.programodawca.drivergear.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.programodawca.drivergear.model.ClothingType;

import java.util.List;

@Repository
public interface ClothingTypeRepository extends JpaRepository<ClothingType, Long> {
    /**
     * Find all active clothing types
     * @return List of active clothing types
     */
    List<ClothingType> findByActiveTrue();

    /**
     * Find clothing type by name
     * @param name Clothing type name
     * @return Clothing type with the given name
     */
    ClothingType findByName(String name);

    /**
     * Check if a clothing type with the given name exists
     * @param name Clothing type name
     * @return true if a clothing type with the given name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Check if a clothing type with the given name exists, excluding the one with the given ID
     * @param name Clothing type name
     * @param id Clothing type ID to exclude
     * @return true if a clothing type with the given name exists (excluding the one with the given ID), false otherwise
     */
    boolean existsByNameAndIdNot(String name, Long id);

    /**
     * Find clothing type by barcode
     * @param barcode Clothing type barcode
     * @return Clothing type with the given barcode
     */
    ClothingType findByBarcode(String barcode);

    /**
     * Check if a clothing type with the given barcode exists
     * @param barcode Clothing type barcode
     * @return true if a clothing type with the given barcode exists, false otherwise
     */
    boolean existsByBarcode(String barcode);

    /**
     * Check if a clothing type with the given barcode exists, excluding the one with the given ID
     * @param barcode Clothing type barcode
     * @param id Clothing type ID to exclude
     * @return true if a clothing type with the given barcode exists (excluding the one with the given ID), false otherwise
     */
    boolean existsByBarcodeAndIdNot(String barcode, Long id);

}
