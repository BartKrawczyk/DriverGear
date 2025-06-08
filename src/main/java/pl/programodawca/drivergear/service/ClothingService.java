package pl.programodawca.drivergear.service;

import pl.programodawca.drivergear.model.Clothing;

public interface ClothingService {

    // Podstawowe operacje
    Clothing findById(Long id);
    Clothing addClothing(Clothing clothing);

    // Operacje magazynowe
    void addClothingToStorage(Long clothingId, int quantity); // Dodanie do magazynu
    void issueClothing(Long clothingId, int quantity);        // Wydanie z magazynu
    void receiveClothing(Long clothingId, int quantity);      // Przyjęcie na magazyn

    // Obsługa kodów kreskowych
    void scanBarcode(String barcode); // Rozpoznawanie kodu kreskowego
}