package ntlong.service;

import ntlong.dto.MyCourseDetailDTO;
import ntlong.dto.MyLearningHistoryDTO;

import java.io.InputStream;

public interface MyCourseService {
    MyCourseDetailDTO findMyCourseDetail(String username, Long courseId, Boolean isPreview);
    void modifyMyLearningHistory(MyLearningHistoryDTO myLearningHistoryDTO, String username);
    InputStream downloadFile(String fileURL);
}
