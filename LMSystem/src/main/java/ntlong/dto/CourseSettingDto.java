package ntlong.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class CourseSettingDto {

    private boolean activated;
    private boolean certificated;
    private boolean historyRecorded;
    private int inactiveDay;
    private boolean downloadable;
    private boolean completeRecord;
    private boolean sequentialCompletion;
    private int completeDuration;
    private int completeUnit;
}
