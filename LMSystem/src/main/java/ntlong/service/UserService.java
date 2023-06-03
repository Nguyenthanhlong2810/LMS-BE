package ntlong.service;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import ntlong.dto.TokenDTO;
import ntlong.dto.UserFirstLoginDTO;
import ntlong.dto.UserInforDTO;
import ntlong.exception.CustomException;
import ntlong.exception.ResourceNotFoundException;
import ntlong.exception.UploadFailException;
import ntlong.exception.UserNotFoundException;
import ntlong.model.*;
import ntlong.payload.request.RegisterRequest;
import ntlong.repository.*;
import ntlong.response.BaseResponse;
import ntlong.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@Service
@AllArgsConstructor
public class UserService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final MailService mailService;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final SkillRepository skillRepository;
    private final ExperienceRepository experienceRepository;

    private final AmazonClient amazonClient;

    private Gson gson = new Gson();

    private final VerificationTokenRepository verificationTokenRepository;

    public TokenDTO signin(AppUser appUser) {
        TokenDTO tokenDTO = new TokenDTO("");
        try {
            if (appUser.getUsername().isBlank() || appUser.getPassword().isBlank()) {
                log.error("Invalid username/password supplied" + HttpStatus.UNPROCESSABLE_ENTITY);
                tokenDTO.setError("Tên người dùng / mật khẩu được cung cấp không hợp lệ");
                tokenDTO.setHttpStatus(HttpStatus.UNPROCESSABLE_ENTITY);
                tokenDTO.setHttpCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
                return tokenDTO;
            } else {
                String username = appUser.getUsername();
                String password = appUser.getPassword();

                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
                tokenDTO.setToken(jwtTokenProvider.createToken(username, appUser.getAppUserRoles()));
                tokenDTO.setHttpCode(HttpStatus.OK.value());
            }
        }catch (BadCredentialsException ex){
            tokenDTO.setError("Tên người dùng / mật khẩu được cung cấp không hợp lệ");
            tokenDTO.setHttpStatus(HttpStatus.UNPROCESSABLE_ENTITY);
            tokenDTO.setHttpCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
        }
        return tokenDTO;
    }

    public String signup(AppUser appUser) {
        if (!userRepository.existsByUsernameAndDeletedFalseAndEnabledTrue(appUser.getUsername())) {
            appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
            userRepository.save(appUser);
            return jwtTokenProvider.createToken(appUser.getUsername(), appUser.getAppUserRoles());
        } else {
            log.error("Username is already in use ");
            throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public void delete(String username) {
        userRepository.deleteByUsername(username);
    }

    public AppUser search(String username) {
        AppUser appUser = userRepository.findByUsernameAndEnabledTrueAndDeletedFalse(username);
        if (appUser == null) {
            throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
        }
        return appUser;
    }

    public AppUser whoami(HttpServletRequest req) {
        return userRepository.findByUsernameAndEnabledTrueAndDeletedFalse(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
    }

    public String refresh(String username) {
        return jwtTokenProvider.createToken(username, userRepository.findByUsernameAndEnabledTrueAndDeletedFalse(username).getAppUserRoles());
    }

    public UserInforDTO getUserById(Long id) throws UserNotFoundException {
        Optional<AppUser> appUser = userRepository.findById(id);
        if(appUser.isEmpty()){
            throw new UserNotFoundException("user not found with id: " + id);
        }
        UserInforDTO dto = new UserInforDTO();
        dto.setId(appUser.get().getId());
        dto.setUsername(appUser.get().getUsername());
        dto.setEmail(appUser.get().getEmail());
        dto.setAppUserRoles(appUser.get().getAppUserRoles());
        dto.setSkillsInteresting(appUser.get().getSkillsInteresting());
        dto.setExperiences(appUser.get().getExperiences());
        dto.setLearningPath(appUser.get().getLearningPath());
        dto.setFullname(appUser.get().getFullname());
        dto.setSite(appUser.get().getSite());
        dto.setPosition(appUser.get().getPosition());
        dto.setBirthdate(appUser.get().getBirthdate());
        dto.setPhoneNumber(appUser.get().getPhoneNumber());

        dto.setAvatarUrl(appUser.get().getAvatarUrl());
        dto.setFacebook(appUser.get().getFacebook());
        return dto;
    }
    public UserInforDTO getUserProfileByCurrenUser(String username) throws UserNotFoundException {
        AppUser appUser = userRepository.findByUsernameAndEnabledTrueAndDeletedFalse(username);
        if(appUser==null){
            throw new UserNotFoundException("User not found");
        }
        UserInforDTO dto = new UserInforDTO();
        dto.setId(appUser.getId());
        dto.setUsername(appUser.getUsername());
        dto.setEmail(appUser.getEmail());
        dto.setAppUserRoles(appUser.getAppUserRoles());
        dto.setSkillsInteresting(appUser.getSkillsInteresting());
        dto.setExperiences(appUser.getExperiences());
        dto.setLearningPath(appUser.getLearningPath());
        dto.setFullname(appUser.getFullname());

        dto.setBirthdate(appUser.getBirthdate());
        dto.setPhoneNumber(appUser.getPhoneNumber());

        dto.setAvatarUrl(appUser.getAvatarUrl());
        dto.setFacebook(appUser.getFacebook());
        dto.setFirstLoginSetup(appUser.isFirstLoginSetup());
        return dto;
    }

    public AppUser updateUser(String appUser, MultipartFile file) throws UploadFailException, IOException {
        AppUser app = gson.fromJson(appUser, AppUser.class);
        if (!userRepository.existsByUsernameAndDeletedFalseAndEnabledTrue(app.getUsername())) {
            throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
        }
        AppUser oldUser = userRepository.findByUsernameAndEnabledTrueAndDeletedFalse(app.getUsername());
        app.setPassword(oldUser.getPassword());

        if(file != null && !file.isEmpty()) {
            app.setAvatarUrl(amazonClient.uploadFile(file).getPreviewUrl());
        }
        // lazy way to resolve error (must edit in next phase)
        Set<Skill> skills = new HashSet<>();
        app.getSkillsInteresting().forEach(skill -> {
            if(Objects.nonNull(skill)) {
                skills.add(skillRepository.findById(skill.getId()).get());
            }
        });

        Set<Experience> experiences = new HashSet<>();
        app.getExperiences().forEach(e -> {
            if(Objects.nonNull(e)) {
                experiences.add(experienceRepository.findById(e.getId()).get());
            }
        });

        app.setSkillsInteresting(skills);
        app.setExperiences(experiences);
        app.setFirstLoginSetup(true);
        app.setEnabled(true);
        return userRepository.save(app);
    }


    public ResponseEntity<BaseResponse> setUserFirstLogin(UserFirstLoginDTO userFirstLoginDTO, String username) {
        AppUser appUser = userRepository.findByUsernameAndEnabledTrueAndDeletedFalse(username);
        Set<String> experiences = userFirstLoginDTO.getExperiences();
        Set<Experience> experienceSet = new HashSet<>(experienceRepository.getExperiencesByName(experiences));

        Set<String> skills = userFirstLoginDTO.getSkills();
        Set<Skill> skillSet = new HashSet<>();
        for(String skillName :  skills){
            Skill skill = skillRepository.findByNameAndDeletedFalse(skillName);
            if(Objects.isNull(skill)){
                Skill newSkill = new Skill();
                newSkill.setDeleted(false);
                newSkill.setName(skillName);
                newSkill.setAdminCreated(false);
                Skill savedSkill = skillRepository.save(newSkill);
                skillSet.add(savedSkill);
            }else{
                skillSet.add(skill);
            }
        }
        appUser.setSkillsInteresting(skillSet);
        appUser.setExperiences(experienceSet);
        appUser.setLearningPath(userFirstLoginDTO.getLearningPath());

        appUser.setFirstLoginSetup(true);

        userRepository.save(appUser);

        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(),
                "Save setting successfully", null));
    }

    public void signUp(RegisterRequest registerRequest) {
        try {
            if (!registerRequest.getConfirmPassword().equals(registerRequest.getPassword())) {
                throw new CustomException("Mật khẩu không khớp", HttpStatus.BAD_REQUEST);
            }

            if (userRepository.existsByEmailAndDeletedFalseAndEnabledTrue(registerRequest.getEmail())){
                throw new CustomException("Email đã tồn tại", HttpStatus.BAD_REQUEST);
            }

            if (userRepository.existsByUsernameAndDeletedFalseAndEnabledTrue(registerRequest.getUsername())){
                throw new CustomException("Tài khoản đã tồn tại", HttpStatus.BAD_REQUEST);
            }

            if (Objects.isNull(registerRequest.getFullname())) {
                throw new CustomException("Họ tên không được để trống", HttpStatus.BAD_REQUEST);
            }

            AppUser user = new AppUser();
            user.setUsername(registerRequest.getUsername());
            user.setFullname(registerRequest.getFullname());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setEnabled(false);
            List<AppUserRole> appUserRoles = registerRequest.getAppUserRoles();
            if (appUserRoles == null || appUserRoles.isEmpty()) {
                appUserRoles = new ArrayList<>();
                appUserRoles.add(AppUserRole.ROLE_CLIENT);
            }
            user.setAppUserRoles(appUserRoles);
            userRepository.save(user);

            String token = generateVerificationToken(user);
            mailService.sendMail(new NotificationEmail("Please activate your account",
                    user.getEmail(), "Thank you for signing up to LMS," +
                    "please click on the below url to activate your account:" +
                    "http://localhost:8088/users/account-verification/" + token));
        }catch (CustomException e){
            throw e;
        }
        catch (Exception e){
            log.error("Register account failed",e);
            throw new CustomException("Đăng kí không thành công",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(() -> new CustomException("Invalid Token", HttpStatus.BAD_REQUEST));
        fetchUserAndEnable(verificationToken.get());
    }

    @Transactional
    void fetchUserAndEnable(VerificationToken verificationToken){
        String username = verificationToken.getAppUser().getUsername();
        AppUser appUser = userRepository.findByUsernameAndEnabledFalseAndDeletedFalse(username);
        if(Objects.isNull(appUser)){
            throw new ResourceNotFoundException("User not found");
        }
        appUser.setEnabled(true);
        userRepository.save(appUser);
    }

    private String generateVerificationToken(AppUser user) {
        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setAppUser(user);

        verificationTokenRepository.save(verificationToken);

        return token;
    }


    public Page<AppUser> getList(int offset, int limit,String name) {
        Pageable pageable = PageRequest.of(offset - 1,limit);
        Page<AppUser> appUsers = userRepository.getList(pageable, name);
        return appUsers;
    }
}
