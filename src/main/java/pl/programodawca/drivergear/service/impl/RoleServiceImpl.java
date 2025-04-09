package pl.programodawca.drivergear.service.impl;

import org.springframework.stereotype.Service;
import pl.programodawca.drivergear.model.Role;
import pl.programodawca.drivergear.repository.RoleRepository;
import pl.programodawca.drivergear.service.RoleService;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    // Wstrzykiwanie zależności przez konstruktor
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findById(Long id) {
        return roleRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Role not found with id: " + id));
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name).orElseThrow(() ->
                new RuntimeException("Role not found with name: " + name));
    }

    @Override
    public Role save(Role role) {
        // Możesz dodać logikę walidacji lub dodatkowe operacje przed zapisem
        return roleRepository.save(role);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}
