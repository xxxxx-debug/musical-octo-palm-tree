package com.example.bank.dao;

import com.example.bank.domain.PhoneData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {

    List<PhoneData> findByUser_Id(Long userId);

    List<PhoneData> findByUser_IdIn(Collection<Long> userIds);

    long countByUser_Id(Long userId);

    boolean existsByPhoneAndUser_IdNot(String phone, Long userId);

    boolean existsByPhone(String phone);

    Optional<PhoneData> findByIdAndUser_Id(Long id, Long userId);
}
