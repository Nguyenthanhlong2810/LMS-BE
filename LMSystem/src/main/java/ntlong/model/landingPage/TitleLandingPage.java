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
@Table(name = "title_landing_page")
public class TitleLandingPage extends BaseEntity {

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landing_page_id", nullable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonBackReference
    private LandingPageSetting landingPageSetting;
}
