package com.example.bank.dao.spec;

import com.example.bank.domain.EmailData;
import com.example.bank.domain.PhoneData;
import com.example.bank.domain.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public final class UserSearchSpecification {

    private UserSearchSpecification() {
    }

    public static Specification<User> from(UserSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getDateOfBirthAfter() != null) {
                predicates.add(cb.greaterThan(root.get("dateOfBirth"), criteria.getDateOfBirthAfter()));
            }
            if (criteria.getNamePrefix() != null && !criteria.getNamePrefix().isBlank()) {
                predicates.add(cb.like(root.get("name"), criteria.getNamePrefix() + "%"));
            }
            if (criteria.getEmail() != null && !criteria.getEmail().isBlank()) {
                Join<User, EmailData> emails = root.join("emails", JoinType.INNER);
                predicates.add(cb.equal(emails.get("email"), criteria.getEmail()));
            }
            if (criteria.getPhone() != null && !criteria.getPhone().isBlank()) {
                Join<User, PhoneData> phones = root.join("phones", JoinType.INNER);
                predicates.add(cb.equal(phones.get("phone"), criteria.getPhone()));
            }

            if (query != null && (criteria.getEmail() != null || criteria.getPhone() != null)) {
                query.distinct(true);
            }

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
