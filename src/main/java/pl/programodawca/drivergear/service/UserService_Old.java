package pl.programodawca.drivergear.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.programodawca.drivergear.model.User_Old;
import pl.programodawca.drivergear.repository.UserRepository;

import java.util.List;

@Service
public class UserService_Old {

    private final UserRepository userRepository;

    @Autowired
    public UserService_Old(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User_Old> findAllUsers() {
        return userRepository.findAll();
    }
}
