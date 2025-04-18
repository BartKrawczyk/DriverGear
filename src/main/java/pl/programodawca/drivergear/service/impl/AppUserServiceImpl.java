package pl.programodawca.drivergear.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.programodawca.drivergear.dto.*;
import pl.programodawca.drivergear.exception.PasswordMismatchException;
import pl.programodawca.drivergear.exception.UserNotFoundException;
import pl.programodawca.drivergear.model.AppUser;
import pl.programodawca.drivergear.repository.AppUserRepository;
import pl.programodawca.drivergear.service.AppUserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AppUserServiceImpl(AppUserRepository appUserRepository,
                              PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AppUserDTO createUser(CreateUserDTO createUserDTO) {
        log.info("Próba utworzenia nowego użytkownika: {}", createUserDTO.getUsername());

        try {
            if (appUserRepository.existsByUsername(createUserDTO.getUsername())) {
                log.error("Użytkownik o nazwie {} już istnieje", createUserDTO.getUsername());
                throw new RuntimeException("Użytkownik o podanej nazwie już istnieje");
            }

            AppUser user = new AppUser();
            user.setUsername(createUserDTO.getUsername());
            user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
            user.setFirstName(createUserDTO.getFirstName());
            user.setLastName(createUserDTO.getLastName());
            user.setRole(createUserDTO.getRole());
            user.setLocked(false);

            AppUser savedUser = appUserRepository.save(user);
            log.info("Pomyślnie utworzono użytkownika: {}", user.getUsername());
            return mapToDTO(savedUser);
        } catch (Exception e) {
            log.error("Błąd podczas tworzenia użytkownika: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AppUserDTO getUserById(Long id) {
        return appUserRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new UserNotFoundException("Nie znaleziono użytkownika o ID: " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public AppUserDTO findById(Long id) {
        log.debug("Wyszukiwanie użytkownika o ID: {}", id);
        return appUserRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new UserNotFoundException("Nie znaleziono użytkownika o ID: " + id));
    }


    @Transactional(readOnly = true)
    @Override
    public UpdateUserDTO getUserForEdit(Long id) {
        log.info("Pobieranie danych użytkownika do edycji, ID: {}", id);
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Nie znaleziono użytkownika o ID: " + id));

        UpdateUserDTO updateUserDTO = UpdateUserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .locked(user.isLocked())
                .build();

        log.debug("Pobrano dane do edycji dla użytkownika: {}", updateUserDTO);
        return updateUserDTO;
    }

    @Override
    @Transactional
    public void updateUser(Long id, UpdateUserDTO updateUserDTO) {
        log.info("Aktualizacja użytkownika o ID: {}", id);

        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Nie znaleziono użytkownika o ID: " + id));

        user.setFirstName(updateUserDTO.getFirstName());
        user.setLastName(updateUserDTO.getLastName());
        user.setRole(updateUserDTO.getRole());
        user.setLocked(updateUserDTO.isLocked());

        appUserRepository.save(user);
        log.info("Pomyślnie zaktualizowano użytkownika o ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public AppUserDTO getUserByUsername(String username) {
        return appUserRepository.findByUsername(username)
                .map(this::mapToDTO)
                .orElseThrow(() -> new UserNotFoundException("Nie znaleziono użytkownika o nazwie: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppUserDTO> getAllUsers() {
        return appUserRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Próba usunięcia użytkownika o ID: {}", id);
        if (!appUserRepository.existsById(id)) {
            throw new UserNotFoundException("Nie znaleziono użytkownika o ID: " + id);
        }
        appUserRepository.deleteById(id);
        log.info("Pomyślnie usunięto użytkownika o ID: {}", id);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordDTO changePasswordDTO) {
        log.info("Próba zmiany hasła dla użytkownika o ID: {}", userId);
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Nie znaleziono użytkownika o ID: " + userId));

        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())) {
            throw new PasswordMismatchException("Aktualne hasło jest nieprawidłowe");
        }

        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new PasswordMismatchException("Nowe hasło i potwierdzenie nie są zgodne");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        appUserRepository.save(user);
        log.info("Pomyślnie zmieniono hasło dla użytkownika o ID: {}", userId);
    }

    @Override
    public void toggleUserStatus(Long id) {
        log.info("Próba przełączenia statusu blokady dla użytkownika o ID: {}", id);
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Nie znaleziono użytkownika o ID: " + id));

        user.setLocked(!user.isLocked());
        appUserRepository.save(user);
        log.info("Pomyślnie przełączono status blokady dla użytkownika o ID: {}", id);
    }

    private AppUserDTO mapToDTO(AppUser user) {
        return AppUserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .locked(user.isLocked())
                .build();
    }
}
