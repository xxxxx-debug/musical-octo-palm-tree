package com.example.bank.service;

import com.example.bank.api.dto.ContactEmailRequest;
import com.example.bank.api.dto.ContactPhoneRequest;
import com.example.bank.api.dto.EmailItemResponse;
import com.example.bank.api.dto.PhoneItemResponse;
import com.example.bank.api.dto.UserProfileResponse;
import com.example.bank.api.error.BusinessException;
import com.example.bank.config.CacheNames;
import com.example.bank.dao.EmailDataRepository;
import com.example.bank.dao.PhoneDataRepository;
import com.example.bank.dao.UserRepository;
import com.example.bank.domain.EmailData;
import com.example.bank.domain.PhoneData;
import com.example.bank.domain.User;
import com.example.bank.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnBean(UserRepository.class)
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final EmailDataRepository emailDataRepository;
    private final PhoneDataRepository phoneDataRepository;

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findProfileById(userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));
        return UserMapper.toProfile(user);
    }

    @CacheEvict(cacheNames = CacheNames.USER_SEARCH, allEntries = true)
    @Transactional
    public EmailItemResponse addEmail(Long userId, ContactEmailRequest request) {
        User user = loadUser(userId);
        String email = normalizeEmail(request.getEmail());
        if (emailDataRepository.existsByEmail(email)) {
            throw new BusinessException(HttpStatus.CONFLICT, "Email is already in use");
        }
        EmailData emailData = new EmailData();
        emailData.setUser(user);
        emailData.setEmail(email);
        return UserMapper.toEmailItem(emailDataRepository.save(emailData));
    }

    @CacheEvict(cacheNames = CacheNames.USER_SEARCH, allEntries = true)
    @Transactional
    public EmailItemResponse updateEmail(Long userId, Long emailId, ContactEmailRequest request) {
        EmailData emailData = emailDataRepository.findByIdAndUser_Id(emailId, userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Email not found"));
        String email = normalizeEmail(request.getEmail());
        if (emailDataRepository.existsByEmailAndUser_IdNot(email, userId)) {
            throw new BusinessException(HttpStatus.CONFLICT, "Email is already in use");
        }
        emailData.setEmail(email);
        return UserMapper.toEmailItem(emailDataRepository.save(emailData));
    }

    @CacheEvict(cacheNames = CacheNames.USER_SEARCH, allEntries = true)
    @Transactional
    public void deleteEmail(Long userId, Long emailId) {
        EmailData emailData = emailDataRepository.findByIdAndUser_Id(emailId, userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Email not found"));
        if (emailDataRepository.countByUser_Id(userId) <= 1) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Cannot delete the last email");
        }
        emailDataRepository.delete(emailData);
    }

    @CacheEvict(cacheNames = CacheNames.USER_SEARCH, allEntries = true)
    @Transactional
    public PhoneItemResponse addPhone(Long userId, ContactPhoneRequest request) {
        User user = loadUser(userId);
        String phone = request.getPhone().trim();
        if (phoneDataRepository.existsByPhone(phone)) {
            throw new BusinessException(HttpStatus.CONFLICT, "Phone is already in use");
        }
        PhoneData phoneData = new PhoneData();
        phoneData.setUser(user);
        phoneData.setPhone(phone);
        return UserMapper.toPhoneItem(phoneDataRepository.save(phoneData));
    }

    @CacheEvict(cacheNames = CacheNames.USER_SEARCH, allEntries = true)
    @Transactional
    public PhoneItemResponse updatePhone(Long userId, Long phoneId, ContactPhoneRequest request) {
        PhoneData phoneData = phoneDataRepository.findByIdAndUser_Id(phoneId, userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Phone not found"));
        String phone = request.getPhone().trim();
        if (phoneDataRepository.existsByPhoneAndUser_IdNot(phone, userId)) {
            throw new BusinessException(HttpStatus.CONFLICT, "Phone is already in use");
        }
        phoneData.setPhone(phone);
        return UserMapper.toPhoneItem(phoneDataRepository.save(phoneData));
    }

    @CacheEvict(cacheNames = CacheNames.USER_SEARCH, allEntries = true)
    @Transactional
    public void deletePhone(Long userId, Long phoneId) {
        PhoneData phoneData = phoneDataRepository.findByIdAndUser_Id(phoneId, userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Phone not found"));
        if (phoneDataRepository.countByUser_Id(userId) <= 1) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Cannot delete the last phone");
        }
        phoneDataRepository.delete(phoneData);
    }

    private User loadUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
