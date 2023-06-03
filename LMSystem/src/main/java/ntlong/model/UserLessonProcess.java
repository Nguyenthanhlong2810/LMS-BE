package ntlong.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_lesson_process")
public class UserLessonProcess extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assign_course_id")
    private AssignCourse assignCourse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_structure_id")
    private LessonStructure lessonStructure;

    @Column(name = "is_completed")
    private Boolean isCompleted;
}
