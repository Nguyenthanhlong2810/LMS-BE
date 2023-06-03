package ntlong.repository;

import ntlong.model.AdminSettingFirstLogin;
import ntlong.model.Experience;
import ntlong.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdminSettingFirstLoginRepository extends JpaRepository<AdminSettingFirstLogin, Long> {
    @Query("select afl.skills from AdminSettingFirstLogin afl")
    List<Skill> getAdminSkills();

    @Query("select afl.experiences from AdminSettingFirstLogin afl")
    List<Experience> getAdminExperiences();
}
