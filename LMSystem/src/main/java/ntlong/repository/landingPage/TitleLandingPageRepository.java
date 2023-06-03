package ntlong.repository.landingPage;

import ntlong.model.landingPage.TitleLandingPage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TitleLandingPageRepository extends JpaRepository<TitleLandingPage, Long> {
    List<TitleLandingPage> getTitleLandingPagesByLandingPageSettingId(Long landingPageId);

    void deleteAllByLandingPageSettingId(Long landingPageId);
}
