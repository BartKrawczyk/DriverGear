package pl.programodawca.drivergear.service;

import pl.programodawca.drivergear.model.User;

import java.util.List;

public interface UserService {
    User findById(Long id);
    List<User> findAll();
    User save(User user);
    User update(Long id, User user);
    void delete(Long id);
}