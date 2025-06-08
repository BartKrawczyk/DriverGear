package pl.programodawca.drivergear.service;

import org.springframework.transaction.annotation.Transactional;
import pl.programodawca.drivergear.dto.AppUserDTO;
import pl.programodawca.drivergear.dto.CreateUserDTO;
import pl.programodawca.drivergear.dto.UpdateUserDTO;
import pl.programodawca.drivergear.dto.ChangePasswordDTO;

import java.util.List;

public interface AppUserService {
    AppUserDTO createUser(CreateUserDTO createUserDTO);
    AppUserDTO getUserById(Long id);
    AppUserDTO getUserByUsername(String username);
    List<AppUserDTO> getAllUsers();

    @Transactional(readOnly = true)
    AppUserDTO findById(Long id);

    @Transactional(readOnly = true)
    UpdateUserDTO getUserForEdit(Long id);

    void updateUser(Long id, UpdateUserDTO updateUserDTO);
    void deleteUser(Long id);
    void changePassword(Long userId, ChangePasswordDTO changePasswordDTO);
    void toggleUserStatus(Long id);
}