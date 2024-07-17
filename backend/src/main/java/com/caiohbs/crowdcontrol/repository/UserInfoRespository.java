package com.caiohbs.crowdcontrol.repository;

import com.caiohbs.crowdcontrol.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRespository extends JpaRepository<UserInfo, Long> {
}
