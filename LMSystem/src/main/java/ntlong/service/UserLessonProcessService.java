package ntlong.service;

import ntlong.dto.lessonprocess.UserLessonProcessDTO;
import ntlong.payload.response.lesson.LessonProcessResponse;

public interface UserLessonProcessService {
    LessonProcessResponse createLessonProcess(UserLessonProcessDTO userLessonProcessDTO);

    LessonProcessResponse updateLessonProcess(UserLessonProcessDTO userLessonProcessDTO);
}
