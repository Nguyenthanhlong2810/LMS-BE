package ntlong;

import java.sql.Timestamp;
import java.util.Collections;

import ntlong.repository.UserRepository;
import ntlong.utils.AccountEnum;
import lombok.RequiredArgsConstructor;
import ntlong.model.AppUser;
import ntlong.model.AppUserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ntlong.service.UserService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@RequiredArgsConstructor
@EnableScheduling
@EnableAsync
public class LmsApp implements CommandLineRunner {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final UserService userService;
    private final UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(LmsApp.class, args);
    }


    @Override
    public void run(String... params) {
        for (AccountEnum accountEnum : AccountEnum.values()) {
            if (!userRepository.existsByUsernameAndDeletedFalseAndEnabledTrue(accountEnum.getUsername())) {
                AppUser user = new AppUser();
                user.setUsername(accountEnum.getUsername());
                user.setPassword(accountEnum.getPassword());
                user.setEmail(accountEnum.getEmail());
                user.setCreatedBy("admin");
                user.setDeleted(false);
                user.setCreatedDate(new Timestamp(System.currentTimeMillis()));
                user.setEnabled(true);
                switch (accountEnum.getRole()) {
                    case "ROLE_CLIENT":
                        user.setAppUserRoles(Collections.singletonList(AppUserRole.ROLE_CLIENT));
                        break;
                    case "ROLE_ADMIN":
                        user.setAppUserRoles(Collections.singletonList(AppUserRole.ROLE_ADMIN));
                        break;
                    default:
                        break;
                }
                userService.signup(user);
            }
        }

    }

}
