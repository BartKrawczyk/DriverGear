package pl.programodawca.drivergear.service.impl;

import org.springframework.stereotype.Service;
import pl.programodawca.drivergear.model.Storage;
import pl.programodawca.drivergear.repository.StorageRepository;
import pl.programodawca.drivergear.service.StorageService;

import java.util.List;

@Service
public class StorageServiceImpl implements StorageService {

    private final StorageRepository storageRepository;

    public StorageServiceImpl(StorageRepository storageRepository) {
        this.storageRepository = storageRepository;
    }

    @Override
    public List<Storage> findAll() {
        return storageRepository.findAll();
    }

    @Override
    public Storage findByClothingId(Long clothingId) {
        return storageRepository.findByClothingId(clothingId);
    }

    @Override
    public Storage saveOrUpdate(Storage storage) {
        return storageRepository.save(storage);
    }

    @Override
    public void deleteByClothingId(Long clothingId) {
        storageRepository.deleteByClothingId(clothingId);
    }
}
