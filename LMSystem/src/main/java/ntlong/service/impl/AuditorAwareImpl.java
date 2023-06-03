package ntlong.service.impl;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Optional<String> getCurrentAuditor() {
        String username = "admin";
        if (SecurityContextHolder.getContext().getAuthentication() != null)
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        return Optional.ofNullable(username);
    }
}
