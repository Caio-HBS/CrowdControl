package com.caiohbs.crowdcontrol.dto.mapper;

import com.caiohbs.crowdcontrol.dto.RoleDTO;
import com.caiohbs.crowdcontrol.model.Role;
import com.caiohbs.crowdcontrol.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
public class RoleDTOMapper implements Function<Role, RoleDTO> {

    private final UserRepository userRepository;

    public RoleDTOMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public RoleDTO apply(Role role) {
        List<String> usersList = userRepository
                .findUsernamesByRoleId(role.getRoleId());

        return new RoleDTO(
                role.getRoleId(),
                role.getRoleName(),
                role.getMaxNumberOfUsers(),
                role.getSalary(),
                role.getPermissions(),
                usersList.stream().toList()
        );
    }
}
