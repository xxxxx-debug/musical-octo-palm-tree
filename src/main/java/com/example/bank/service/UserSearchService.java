package com.example.bank.service;

import com.example.bank.api.dto.PageResponse;
import com.example.bank.api.dto.UserSearchItemResponse;
import com.example.bank.api.error.BusinessException;
import com.example.bank.dao.EmailDataRepository;
import com.example.bank.dao.PhoneDataRepository;
import com.example.bank.dao.UserRepository;
import com.example.bank.dao.spec.UserSearchCriteria;
import com.example.bank.config.CacheNames;
import com.example.bank.dao.spec.UserSearchSpecification;
import com.example.bank.domain.EmailData;
import com.example.bank.domain.PhoneData;
import com.example.bank.domain.User;
import com.example.bank.service.mapper.UserMapper;
import com.example.bank.util.DateFormatUtils;
import com.example.bank.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@ConditionalOnBean(UserRepository.class)
@RequiredArgsConstructor
public class UserSearchService {

    private final UserRepository userRepository;
    private final EmailDataRepository emailDataRepository;
    private final PhoneDataRepository phoneDataRepository;

    @Cacheable(
            cacheNames = CacheNames.USER_SEARCH,
            key = "#dateOfBirth + '|' + #phone + '|' + #name + '|' + #email + '|' + #page + '|' + #size",
            unless = "#result == null"
    )
    @Transactional(readOnly = true)
    public PageResponse<UserSearchItemResponse> search(String dateOfBirth,
                                                       String phone,
                                                       String name,
                                                       String email,
                                                       Integer page,
                                                       Integer size) {
        if (name != null && name.isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "name filter must not be blank");
        }

        UserSearchCriteria criteria = UserSearchCriteria.builder()
                .dateOfBirthAfter(dateOfBirth != null && !dateOfBirth.isBlank()
                        ? DateFormatUtils.parse(dateOfBirth) : null)
                .phone(trimToNull(phone))
                .namePrefix(trimToNull(name))
                .email(trimToNull(email))
                .build();

        PageRequest pageRequest = PaginationUtils.toPageRequest(page, size);
        Specification<User> specification = UserSearchSpecification.from(criteria);
        Page<User> users = userRepository.findAll(specification, pageRequest);

        List<Long> userIds = users.getContent().stream().map(User::getId).collect(Collectors.toList());
        Map<Long, List<EmailData>> emailsByUser = groupEmails(userIds);
        Map<Long, List<PhoneData>> phonesByUser = groupPhones(userIds);

        List<UserSearchItemResponse> content = users.getContent().stream()
                .map(user -> UserMapper.toSearchItem(
                        user,
                        emailsByUser.getOrDefault(user.getId(), Collections.emptyList()),
                        phonesByUser.getOrDefault(user.getId(), Collections.emptyList())))
                .collect(Collectors.toList());

        PageResponse<UserSearchItemResponse> response = PageResponse.<UserSearchItemResponse>builder()
                .content(content)
                .page(PaginationUtils.toApiPage(users.getNumber()))
                .size(users.getSize())
                .totalElements(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .build();

        log.debug("User search: filters=[dob>{}, phone={}, name={}, email={}], page={}, hits={}",
                criteria.getDateOfBirthAfter(), criteria.getPhone(), criteria.getNamePrefix(),
                criteria.getEmail(), response.getPage(), response.getTotalElements());
        return response;
    }

    private Map<Long, List<EmailData>> groupEmails(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return emailDataRepository.findByUser_IdIn(userIds).stream()
                .collect(Collectors.groupingBy(email -> email.getUser().getId()));
    }

    private Map<Long, List<PhoneData>> groupPhones(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return phoneDataRepository.findByUser_IdIn(userIds).stream()
                .collect(Collectors.groupingBy(phone -> phone.getUser().getId()));
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
