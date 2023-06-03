package ntlong.repository.landingPage;

import ntlong.model.landingPage.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback,Long> {
    List<Feedback> getFeedbacksByLandingPageSettingId(Long landingPageId);

    void deleteAllByLandingPageSettingId(Long landingPageId);

    List<Feedback> findAllByLandingPageSettingId(Long landingPageId);
}
