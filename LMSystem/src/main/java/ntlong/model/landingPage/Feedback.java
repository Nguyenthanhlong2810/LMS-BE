package ntlong.model.landingPage;

import ntlong.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "feedback")
public class Feedback extends BaseEntity {

    @Column(name = "image_learner_link")
    private String imageLearnerLink;

    @Column(name = "image_leaner_name")
    private String imageLearnerName;

    @Column(name = "learner_name")
    private String learnerName;

    @Column(name = "content_feedback")
    private String contentFeedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landing_page_id", nullable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonBackReference
    private LandingPageSetting landingPageSetting;
}
