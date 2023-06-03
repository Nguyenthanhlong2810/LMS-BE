package ntlong.model;

import ntlong.enums.StatusCourseEnum;
import ntlong.enums.TypeAssignEnum;
import ntlong.enums.UnitTimeEnum;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "assign_course")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssignCourse extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course; // Khóa học

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user_id")
    private AppUser appUser; // Học viên

    @Enumerated(EnumType.STRING)
    @Column(name = "type_assign")
    private TypeAssignEnum typeAssign; // Loại hình gán

    @Column(name = "assign_date")
    private Date assignDate; //Ngày gán

    @Column(name = "from_time")
    private Date fromTime;

    @Column(name = "to_time")
    @Temporal(TemporalType.DATE)
    private Date toTime;

    @Column(name = "progress_status")
    @Enumerated(EnumType.STRING)
    private StatusCourseEnum progressStatus;

    @Column(name= "completed_date")
    private Date completedDate;

    @Column(name= "my_certificate_image_link")
    private String myCertificateImageLink;

    @Column(name= "my_certificate_pdf_link")
    private String myCertificatePdfLink;

    @Column(name= "last_visited_course_page")
    private Date lastVisitedCoursePage;

}
