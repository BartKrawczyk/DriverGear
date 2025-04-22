package pl.programodawca.drivergear.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.programodawca.drivergear.repository.AppUserRepository;
import pl.programodawca.drivergear.dto.CreateUserDTO;
import pl.programodawca.drivergear.dto.AppUserDTO;
import pl.programodawca.drivergear.service.impl.AppUserServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {
    @Mock
    private AppUserRepository userRepository;

    @InjectMocks
    private AppUserServiceImpl userService;  // zamiast private AppUserService userService


    @Test
    void shouldCreateUser() {
        // given
        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("testuser");
        dto.setPassword("Password123!");
        dto.setFirstName("Test");
        dto.setLastName("User");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        // when
        AppUserDTO result = userService.createUser(dto);

        // then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).save(any());
    }
}
