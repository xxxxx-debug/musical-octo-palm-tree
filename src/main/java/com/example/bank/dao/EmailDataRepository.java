package com.example.bank.dao;

import com.example.bank.domain.EmailData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EmailDataRepository extends JpaRepository<EmailData, Long> {

    List<EmailData> findByUser_Id(Long userId);

    List<EmailData> findByUser_IdIn(Collection<Long> userIds);

    long countByUser_Id(Long userId);

    boolean existsByEmailAndUser_IdNot(String email, Long userId);

    boolean existsByEmail(String email);

    Optional<EmailData> findByIdAndUser_Id(Long id, Long userId);
}
