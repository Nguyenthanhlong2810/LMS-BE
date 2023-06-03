package ntlong.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LessonStructure extends BaseEntity {

    @Column(name = "name_content", nullable = false)
    private String nameContent;

    @OneToMany(mappedBy = "lessonStructure", cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.REMOVE})
    private List<LessonContentUpload> lessonContentUploads;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonBackReference
    private Course course;

    @Column(name = "sort_order")
    private long sortOrder;
}
