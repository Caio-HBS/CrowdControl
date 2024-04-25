package com.caiohbs.crowdcontrol.repository;

import com.caiohbs.crowdcontrol.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
