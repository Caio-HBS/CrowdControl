package com.caiohbs.crowdcontrol.repository;

import com.caiohbs.crowdcontrol.model.EmailCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailCodeRepository extends JpaRepository<EmailCode, Long> {

    EmailCode findByEmailCode(String code);

}
