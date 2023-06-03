package ntlong.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignCourseInactiveDTO {
    private Date lastVisitedDate;
    private int inactiveDate;
    private Long assignCourseId;
    private Long courseId;
    private Long appUserId;

}

