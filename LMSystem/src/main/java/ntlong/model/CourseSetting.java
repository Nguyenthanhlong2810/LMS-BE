package ntlong.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Entity
@Table(name = "course_setting")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseSetting extends BaseEntity {

    @Column(name = "id")
    private Long id;

    @Column(name = "is_activated", columnDefinition = "boolean default false")
    private Boolean isActivated;

    @Column(name = "is_certificated", columnDefinition = "boolean default false")
    private Boolean isCertificated = false;

    @Column(name = "is_complete_by_order", columnDefinition = "boolean default false")
    private Boolean isCompleteByOrder = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @NotFound(action= NotFoundAction.IGNORE)
    @JsonIgnore
    private Course course;
}
