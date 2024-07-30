package com.caiohbs.crowdcontrol.repository;

import com.caiohbs.crowdcontrol.model.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;


@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    EntityManager entityManager;
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("Should get a populated List of users successfully from DB")
    void findUsernamesByRoleId_Success() {

        Role createdRole = this.createRole(
                "TEST_ROLE_1", 3, 1000.0,
                List.of("CREATE_ROLE_GENERAL")
        );

        this.createUser(
                "First", "Test", "test1@email.com",
                "123", LocalDate.now().minusYears(18),
                LocalDate.now(), List.of(), List.of(), createdRole
        );
        this.createUser(
                "Second", "Test2", "test2@email.com",
                "456", LocalDate.now().minusYears(18),
                LocalDate.now(), List.of(), List.of(), createdRole
        );
        this.createUser(
                "Third", "Test3", "test3@email.com",
                "789", LocalDate.now().minusYears(18),
                LocalDate.now(), List.of(), List.of(), createdRole
        );

        List<String> foundUsers = this.userRepository.findUsernamesByRoleId(
                createdRole.getRoleId()
        );

        assertThat(foundUsers.size()).isEqualTo(3);

    }

    @Test
    @DisplayName("Should get an empty List of users successfully from DB")
    void findUsernamesByRoleId_Failed() {

        Role createdRole = this.createRole(
                "TEST_ROLE_2", 1, 2000.0,
                List.of("DELETE_GENERAL")
        );

        List<String> foundUsers = this.userRepository.findUsernamesByRoleId(
                createdRole.getRoleId()
        );

        assertThat(foundUsers.size()).isEqualTo(0);

    }

    private void createUser(
            String firstName, String lastName, String email, String password,
            LocalDate birthDate, LocalDate localDate, List<Payment> payments,
            List<SickNote> sickNotes, Role role
    ) {

        User newUser = new User(
                firstName, lastName, email, password, birthDate, localDate,
                payments, sickNotes, role
        );
        this.entityManager.persist(newUser);

    }

    private Role createRole(
            String roleName, int maxNumberOfUsers,
            double salary, List<String> permissions
    ) {

        Role newRole = new Role(roleName, maxNumberOfUsers, salary, permissions);
        this.entityManager.persist(newRole);

        return newRole;

    }

}