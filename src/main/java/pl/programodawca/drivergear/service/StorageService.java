package pl.programodawca.drivergear.service;

import pl.programodawca.drivergear.model.Storage;
import java.util.List;

public interface StorageService {

    // Pobieranie wszystkich pozycji w magazynie
    List<Storage> findAll();

    // Pobranie wpisu w magazynie na podstawie ID ubrania
    Storage findByClothingId(Long clothingId);

    // Dodanie nowej pozycji lub aktualizacja stanu magazynowego
    Storage saveOrUpdate(Storage storage);

    // Usunięcie wpisu w magazynie po identyfikatorze ubrania
    void deleteByClothingId(Long clothingId);
}
