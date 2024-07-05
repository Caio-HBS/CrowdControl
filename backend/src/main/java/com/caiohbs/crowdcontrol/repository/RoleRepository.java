package com.caiohbs.crowdcontrol.repository;

import com.caiohbs.crowdcontrol.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(String roleName);
}
