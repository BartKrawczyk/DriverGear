package pl.programodawca.drivergear.service.impl;

import org.springframework.stereotype.Service;
import pl.programodawca.drivergear.model.User;
import pl.programodawca.drivergear.repository.UserRepository;
import pl.programodawca.drivergear.service.UserService;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new RuntimeException("User not found with id: " + id));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(Long id, User user) {
        User existingUser = findById(id);
        existingUser.setUsername(user.getUsername());
        existingUser.setPassword(user.getPassword());
        // Update other fields if necessary
        return userRepository.save(existingUser);
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}