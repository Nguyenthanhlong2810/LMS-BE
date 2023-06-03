package ntlong.repository.landingPage;

import ntlong.model.landingPage.LandingPageSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LandingPageSettingRepository extends JpaRepository<LandingPageSetting, Long> {
}
