package ntlong.model;


import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@Table(name = "lesson_content_upload")
@AllArgsConstructor
@Getter
@Setter
public class LessonContentUpload extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_structure_id")
    private LessonStructure lessonStructure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_upload_id")
    private ContentUpload contentUpload;

    @Column(name = "sort_order")
    private long sortOrder;

    @Column(name = "can_download")
    private boolean canDownload;

    @Column(name = "compeleted_open")
    private boolean completedOpen;

    @Column(name = "condition_pass")
    private int conditionPass;

    @Column(name = "duration", columnDefinition = "int8")
    private Long duration;
}
