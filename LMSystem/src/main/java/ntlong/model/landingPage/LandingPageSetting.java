package ntlong.model.landingPage;

import ntlong.model.BaseEntity;
import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "landing_page_setting")
public class LandingPageSetting extends BaseEntity {

    @Column(name = "form_login_link")
    private String formLoginLink;

    @Column(name = "form_login_name")
    private String formLoginName;

    @Column(name = "system_purpose_link")
    private String systemPurposeLink;

    @Column(name = "system_purpose_name")
    private String systemPurposeName;

    @Column(name = "introduce_image_link")
    private String introduceImageLink;

    @Column(name = "introduce_image_name")
    private String introduceImageName;
}
