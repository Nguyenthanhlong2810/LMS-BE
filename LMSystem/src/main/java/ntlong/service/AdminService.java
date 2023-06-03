package ntlong.service;

import lombok.AllArgsConstructor;
import ntlong.dto.AdminSettingFirstLoginDTO;
import ntlong.dto.UserFirstLoginDTO;
import ntlong.exception.CustomException;
import ntlong.model.AdminSettingFirstLogin;
import ntlong.model.AppUser;
import ntlong.model.Experience;
import ntlong.model.Skill;
import ntlong.payload.response.AdminSettingFirstLoginResponse;
import ntlong.repository.*;
import ntlong.response.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@AllArgsConstructor
public class AdminService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final ExperienceRepository experienceRepository;

    private final AdminSettingFirstLoginRepository adminSettingFirstLoginRepository;

    @Transactional
    public ResponseEntity<BaseResponse> adminSetFirstLogin(AdminSettingFirstLoginDTO adminFirstLoginSettingDTO){

        //check update experiences
        List<String> removedExperiences = experienceRepository
                .getRemainingExperiences(adminFirstLoginSettingDTO.getExperiences());
        for(String expName : adminFirstLoginSettingDTO.getExperiences()){
            if(!experienceRepository.existsByNameAndDeletedFalse(expName)){
                Experience experience = new Experience();
                experience.setDeleted(false);
                experience.setName(expName);
                experienceRepository.save(experience);
            }else{
                Experience experience = experienceRepository.getExperienceByNameAndDeletedFalse(expName);
                experience.setDeleted(false);
                experienceRepository.save(experience);
            }
        }
        //delete experience
        for(String removeExp : removedExperiences){
            if(experienceRepository.existExperienceInCourse(removeExp)){
                throw new CustomException("Không thể xóa kinh nghiệm "+removeExp +
                        " (Đã tồn tại khóa học có kinh nghiệm này)", HttpStatus.BAD_REQUEST);
            }

            if(experienceRepository.existExperienceInUserExperiences(removeExp)){
                throw new CustomException("Không thể xóa kinh nghiệm "+removeExp +
                        " (Đã có người dùng lựa chọn kinh nghiệm này)", HttpStatus.BAD_REQUEST);
            }
        }
        experienceRepository.updateDeletedExperiences(removedExperiences);

        //check update skills
        List<String> removedSkills = skillRepository
                .getRemainingSkills(adminFirstLoginSettingDTO.getSkills());
        for(String skillName : adminFirstLoginSettingDTO.getSkills()){
            if(!skillRepository.existsByNameAndDeletedFalse(skillName)){
                Skill skill = new Skill();
                skill.setDeleted(false);
                skill.setAdminCreated(true);
                skill.setName(skillName);
                skillRepository.save(skill);
            }else{
                Skill skill = skillRepository.findByNameAndDeletedFalse(skillName);
                skill.setDeleted(false);
                skillRepository.save(skill);
            }
        }
        //delete skills
        for(String removeSkill : removedSkills){
            if(skillRepository.existSkillInCourse(removeSkill)){
                throw new CustomException("Không thể xóa kỹ năng "+removeSkill +
                        " (Đã tồn tại khóa học có kỹ năng này)", HttpStatus.BAD_REQUEST);
            }

            if(skillRepository.existSkillInUserSkills(removeSkill)){
                throw new CustomException("Không thể xóa kỹ năng "+removeSkill +
                        " (Đã có người dùng lựa chọn kỹ năng này)", HttpStatus.BAD_REQUEST);
            }
        }
        skillRepository.updateDeletedSkills(removedSkills);

        List<AdminSettingFirstLogin> adminSettingFirstLogins = adminSettingFirstLoginRepository.findAll();
        AdminSettingFirstLogin adminSettingFirstLogin = new AdminSettingFirstLogin();
        if(adminSettingFirstLogins.size() > 0) {
            adminSettingFirstLogin = adminSettingFirstLogins.get(0);
        }
        Set<String> experiences = adminFirstLoginSettingDTO.getExperiences();
        List<Experience> experienceList = experienceRepository.getExperiencesByName(experiences);

        Set<String> skills = adminFirstLoginSettingDTO.getSkills();
        List<Skill> skillList = skillRepository.getSkillsByName(skills);

        adminSettingFirstLogin.setSkip(adminFirstLoginSettingDTO.isSkip());
        adminSettingFirstLogin.setSkills(skillList);
        adminSettingFirstLogin.setExperiences(experienceList);

        adminSettingFirstLoginRepository.save(adminSettingFirstLogin);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(),
                "Save setting successfully", null));
    }

    public ResponseEntity<BaseResponse> getAdminSettingFirstLogin() {
        List<AdminSettingFirstLogin> adminSettingFirstLogins = adminSettingFirstLoginRepository.findAll();
        AdminSettingFirstLogin adminSettingFirstLogin = new AdminSettingFirstLogin();
        if(adminSettingFirstLogins.size() > 0) {
            adminSettingFirstLogin = adminSettingFirstLogins.get(0);
        }

        AdminSettingFirstLoginResponse response = new AdminSettingFirstLoginResponse();
        response.setExperiences(adminSettingFirstLogin.getExperiences());
        response.setSkills(adminSettingFirstLogin.getSkills());
        response.setSkip(adminSettingFirstLogin.isSkip());
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(),
                "get setting successfully", response));
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

}
