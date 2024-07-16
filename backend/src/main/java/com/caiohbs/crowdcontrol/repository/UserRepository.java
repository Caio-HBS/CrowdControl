package com.caiohbs.crowdcontrol.repository;

import com.caiohbs.crowdcontrol.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u.email FROM User u JOIN u.role r WHERE r.roleId = :roleId")
    List<String> findUsernamesByRoleId(@RequestParam("roleId") Long roleId);

    Optional<User> findByEmail(String email);

}
