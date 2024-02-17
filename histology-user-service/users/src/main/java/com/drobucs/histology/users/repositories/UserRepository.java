package com.drobucs.histology.users.repositories;

import com.drobucs.histology.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User getUserById(Long id);
    User getUserByLogin(String login);

    User getUserByApiKeySha512(String apiKey);

    User getUSerByEmail(String email);
}
