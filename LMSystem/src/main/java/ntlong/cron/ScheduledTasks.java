package ntlong.cron;

import lombok.RequiredArgsConstructor;
import ntlong.enums.StatusCourseEnum;
import ntlong.repository.AssignCourseRepository;
import ntlong.repository.LessonContentUploadHistoryRepository;
import ntlong.repository.LessonContentUploadRepository;
import ntlong.repository.UserLessonProcessRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final AssignCourseRepository assignCourseRepository;

    private final UserLessonProcessRepository userLessonProcessRepository;

    private final LessonContentUploadRepository lessonContentUploadRepository;

    private final LessonContentUploadHistoryRepository lessonContentUploadHistoryRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void checkOverdueCourse(){
        assignCourseRepository.updateOverdueAssignCourse(StatusCourseEnum.OVER_DUE);
    }

//    @Transactional
//    @Scheduled(cron = "0 0 0 * * *")
//    public void resetProgressUserCourse(){
//        List<AssignCourseInactiveDTO> assignCourseList = assignCourseRepository.getAssignCourseInactive(StatusCourseEnum.UNCOMPLETED);
//        for(AssignCourseInactiveDTO ac : assignCourseList){
//            int lastVisitedTimeAgo = (int) Duration.between(LocalDateTime.now(), Instant.ofEpochMilli(ac.getLastVisitedDate().getTime())
//                    .atZone(ZoneId.systemDefault())
//                    .toLocalDateTime()).toDays();
//            if((-1*lastVisitedTimeAgo) > ac.getInactiveDate()) {
//                userLessonProcessRepository.deleteAllByAssignCourseId(ac.getAssignCourseId());
//                List<Long> lessonContentUploadHistory = lessonContentUploadRepository
//                        .getListLessonContentUploadIdByCourseId(ac.getCourseId());
//                lessonContentUploadHistoryRepository.deleteAllByAppUserIdAndLessonContentUploadIds(ac.getAppUserId(),
//                        lessonContentUploadHistory);
//            }
//        }
//    }
}
