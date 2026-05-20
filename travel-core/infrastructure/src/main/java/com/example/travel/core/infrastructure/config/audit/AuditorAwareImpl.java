package com.example.travel.core.infrastructure.config.audit;

import com.example.travel.core.infrastructure.adapter.out.persistence.UserJpaEntity;
import com.example.travel.core.infrastructure.adapter.out.persistence.UserJpaEntity_;
import com.example.travel.core.infrastructure.adapter.out.persistence.UserSpringDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorProvider")
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<UserJpaEntity> {

    private final UserSpringDataRepository userRepository;
    private UserJpaEntity systemUser;

    @Override
    public Optional<UserJpaEntity> getCurrentAuditor() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication instanceof UserToken userToken) {
//            return userRepository.findById(userToken.getUser().getId());
//        }
        if (systemUser == null) {
            systemUser = userRepository.findOne(
                    (root, query, cb) -> cb.equal(root.get(UserJpaEntity_.username), "SYSTEM")).orElse(null);
        }
        return Optional.ofNullable(systemUser);
    }
}