package ntlong.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "note_lesson")
public class NoteLesson extends BaseEntity {

    @Column(name = "time", nullable = false)
    private Long time;
    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_upload_id")
    @JsonBackReference
    private ContentUpload contentUpload;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user_id")
    @JsonBackReference
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_structure_id")
    @JsonBackReference
    private LessonStructure lessonStructure;

}
