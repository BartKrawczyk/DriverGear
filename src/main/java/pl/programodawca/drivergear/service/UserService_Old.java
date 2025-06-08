package pl.programodawca.drivergear.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.programodawca.drivergear.model.AppUser;
import pl.programodawca.drivergear.repository.AppUserRepository;

import java.util.List;

@Service
public class UserService_Old {

    private final AppUserRepository appUserRepository;

    @Autowired
    public UserService_Old(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public List<AppUser> findAllUsers() {
        return appUserRepository.findAll();
    }
}
