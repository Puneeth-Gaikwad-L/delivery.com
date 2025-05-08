package com.delivery.user_service.Repositories;

import com.delivery.user_service.Models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByPhoneNumber(String phoneNumber);

    Optional<Users> findByUserEmailId(String emailId);
}
