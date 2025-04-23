package pl.programodawca.drivergear.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.programodawca.drivergear.model.Clothing;
import pl.programodawca.drivergear.model.Storage;
import pl.programodawca.drivergear.repository.ClothingRepository;
import pl.programodawca.drivergear.repository.StorageRepository;
import pl.programodawca.drivergear.service.ClothingService;

import java.util.Optional;

@Service
public class ClothingServiceImpl implements ClothingService {

    private final ClothingRepository clothingRepository;
    private final StorageRepository storageRepository;

    public ClothingServiceImpl(ClothingRepository clothingRepository, StorageRepository storageRepository) {
        this.clothingRepository = clothingRepository;
        this.storageRepository = storageRepository;
    }

    @Override
    public Clothing findById(Long id) {
        return clothingRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Clothing not found with id: " + id));
    }

    @Override
    public Clothing addClothing(Clothing clothing) {
        return clothingRepository.save(clothing);
    }

    @Override
    @Transactional
    public void addClothingToStorage(Long clothingId, int quantity) {
        Clothing clothing = findById(clothingId);

        // Sprawdzenie, czy dany przedmiot już istnieje w magazynie
        Optional<Storage> storageOptional = Optional.ofNullable(storageRepository.findByClothingId(clothingId));
        Storage storage = storageOptional.orElseGet(() -> new Storage(clothing, 0));

        // Dodajemy ilość
        storage.setQuantity(storage.getQuantity() + quantity);
        storageRepository.save(storage);
    }

    @Override
    @Transactional
    public void issueClothing(Long clothingId, int quantity) {
        Storage storage = storageRepository.findByClothingId(clothingId);
        if (storage == null) {
            throw new RuntimeException("Storage record not found for clothing ID: " + clothingId);
        }

        // Sprawdź dostępną ilość
        if (storage.getQuantity() < quantity) {
            throw new RuntimeException("Not enough items in storage for clothing ID: " + clothingId);
        }

        // Odejmujemy ilość
        storage.setQuantity(storage.getQuantity() - quantity);
        storageRepository.save(storage);
    }

    @Override
    @Transactional
    public void receiveClothing(Long clothingId, int quantity) {
        addClothingToStorage(clothingId, quantity); // Po prostu aktualizujemy ilość
    }

    @Override
    public void scanBarcode(String barcode) {
        // Wyszukiwanie ubrania po kodzie kreskowym używając metody repozytorium
        Clothing clothing = clothingRepository.findByBarcode(barcode)
                .orElseThrow(() -> new RuntimeException("Barcode not found: " + barcode));

        System.out.println("Clothing recognized: " + clothing.getName());
    }

}