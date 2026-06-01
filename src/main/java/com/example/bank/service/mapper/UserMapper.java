package com.example.bank.service.mapper;

import com.example.bank.api.dto.EmailItemResponse;
import com.example.bank.api.dto.PhoneItemResponse;
import com.example.bank.api.dto.UserProfileResponse;
import com.example.bank.api.dto.UserSearchItemResponse;
import com.example.bank.domain.EmailData;
import com.example.bank.domain.PhoneData;
import com.example.bank.domain.User;
import com.example.bank.util.DateFormatUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserProfileResponse toProfile(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .dateOfBirth(DateFormatUtils.format(user.getDateOfBirth()))
                .balance(user.getAccount() != null ? user.getAccount().getBalance() : null)
                .emails(mapEmails(user.getEmails()))
                .phones(mapPhones(user.getPhones()))
                .build();
    }

    public static UserSearchItemResponse toSearchItem(User user, List<EmailData> emails, List<PhoneData> phones) {
        return UserSearchItemResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .dateOfBirth(DateFormatUtils.format(user.getDateOfBirth()))
                .emails(emails.stream().map(EmailData::getEmail).sorted().collect(Collectors.toList()))
                .phones(phones.stream().map(PhoneData::getPhone).sorted().collect(Collectors.toList()))
                .build();
    }

    public static EmailItemResponse toEmailItem(EmailData emailData) {
        return new EmailItemResponse(emailData.getId(), emailData.getEmail());
    }

    public static PhoneItemResponse toPhoneItem(PhoneData phoneData) {
        return new PhoneItemResponse(phoneData.getId(), phoneData.getPhone());
    }

    private static List<EmailItemResponse> mapEmails(List<EmailData> emails) {
        return emails.stream()
                .sorted(Comparator.comparing(EmailData::getEmail))
                .map(UserMapper::toEmailItem)
                .collect(Collectors.toList());
    }

    private static List<PhoneItemResponse> mapPhones(List<PhoneData> phones) {
        return phones.stream()
                .sorted(Comparator.comparing(PhoneData::getPhone))
                .map(UserMapper::toPhoneItem)
                .collect(Collectors.toList());
    }
}
