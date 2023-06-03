package ntlong.service.impl;

import ntlong.converter.LessonStructureConverter;
import ntlong.dto.*;
import ntlong.enums.StatusCourseEnum;
import ntlong.enums.TypeContentUploadEnum;
import ntlong.exception.ResourceNotFoundException;
import ntlong.model.*;
import ntlong.repository.*;
import ntlong.service.MyCourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyCourseServiceImpl implements MyCourseService {

    private final CourseRepository courseRepository;
    private final LessonStructureConverter lessonStructureConverter;
    private final UserLessonProcessRepository userLessonProcessRepository;
    private final UserRepository userRepository;
    private final AssignCourseRepository assignCourseRepository;
    private final LessonContentUploadRepository lessonContentUploadRepository;
    private final LessonContentUploadHistoryRepository lessonContentUploadHistoryRepository;

    private final LessonStructureRepository lessonStructureRepository;

    private final AssignCourseServiceImpl assignCourseService;

    private final CourseSettingRepository courseSettingRepository;

    @Override
    public MyCourseDetailDTO findMyCourseDetail(String username, Long courseId, Boolean isPreview) {
        // Lấy thông tin khóa học
        Course course = courseRepository.findByIdAndDeletedFalse(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Khóa học không tồn tại!!"));
        Long appUserId = getAppUserIdByUserName(username);
        if(Objects.isNull(isPreview) || !isPreview){
            if(!assignCourseRepository.checkAssignedCourse(courseId,appUserId)){
                assignCourseService.assignFreeCourseToUser(course,appUserId);
            }
        }
        AssignCourse assignCourse = assignCourseRepository
                .getAssignCourseByCourseIdAndAppUserIdAndDeletedFalse(courseId,appUserId);
        assignCourse.setProgressStatus(StatusCourseEnum.UNCOMPLETED);
        assignCourseRepository.save(assignCourse);
        // Thêm vào DTO
        MyCourseDetailDTO myCourseDetailDTO = new MyCourseDetailDTO();
        myCourseDetailDTO.setId(course.getId());
        myCourseDetailDTO.setCourseName(course.getName());
        myCourseDetailDTO.setSummary(course.getSummary());
        myCourseDetailDTO.setFileNamePreview(course.getFileNamePreview());
        myCourseDetailDTO.setPathPreview(course.getPathPreview());
        myCourseDetailDTO.setRate(course.getRate());
        CourseSetting courseSetting = courseSettingRepository.findByCourseId(courseId);
        if(Objects.nonNull(courseSetting)){
            myCourseDetailDTO.setCompletedByOrder(courseSetting.getIsCompleteByOrder());
        }
        // Lấy thông tin danh sách bài học set vào DTO
        List<LessonStructure> lessonStructures = course.getLessonStructures();
        List<LessonStructureDTO> lessonStructureDTOS = lessonStructures
                .stream()
                .sorted(Comparator.comparing(LessonStructure::getSortOrder))
                .map(lessonStructureConverter::convertToDTO)
                .collect(Collectors.toList());

        // Lấy trạng thái hoàn thành mỗi tiết học của user
        for (LessonStructureDTO lessonStructureDTO : lessonStructureDTOS) {
            // Lấy trạng thái hoàn thành mỗi content upload của user
            if (!CollectionUtils.isEmpty(lessonStructureDTO.getContentUploads())) {
                lessonStructureDTO.getContentUploads().stream().forEach(contentUploadDTO -> {
                    if (lessonContentUploadHistoryRepository.existsByLessonContentUpload_IdAndAppUser_IdAndIsCompletedTrue(contentUploadDTO.getLessonContentUploadId(), appUserId)) {
                        contentUploadDTO.setCompleted(true);
                    }
                });

            }
        }

        List<ContentUploadDTO> contentUploadDTOS;
        for (LessonStructureDTO lessonStructureDTO : lessonStructureDTOS) {
            // Lấy tổng số tiết của mỗi bài học
            contentUploadDTOS = lessonStructureDTO.getContentUploads() == null ? new ArrayList<>() : lessonStructureDTO.getContentUploads();
            int totalContentUpload = CollectionUtils.isEmpty(contentUploadDTOS) ? 0 : contentUploadDTOS.size();

            lessonStructureDTO.setTotalLessonDetailOfLesson(totalContentUpload);
            // Lấy tổng thời lượng của mỗi bài học
            int totalLessonDetailOfLessonDuration = (int) contentUploadDTOS.stream().mapToLong(ContentUploadDTO::getDuration).sum();
            lessonStructureDTO.setTotalLessonDetailOfLessonDuration(Math.round((float) totalLessonDetailOfLessonDuration));
        }

        myCourseDetailDTO.setLessonStructures(lessonStructureDTOS);

        // Lấy tổng thời lượng khóa học
        int totalCourseDuration = 0;
        for (LessonStructure lessonStructure : lessonStructures) {
            totalCourseDuration += lessonStructure.getLessonContentUploads().stream().mapToLong(LessonContentUpload::getDuration).sum();
        }
        totalCourseDuration = Math.round((float) totalCourseDuration);
        myCourseDetailDTO.setTotalCourseDuration(totalCourseDuration);

        // Lấy tổng số bài học
        myCourseDetailDTO.setLessonTotal(lessonStructures.size());

        // Lấy tổng số tiết của khóa học
        int totalLessonDetailOfCourse = myCourseDetailDTO.getLessonStructures().stream().mapToInt(LessonStructureDTO::getTotalLessonDetailOfLesson).sum();
        myCourseDetailDTO.setTotalLessonDetailOfCourse(totalLessonDetailOfCourse);

        return myCourseDetailDTO;
    }

    @Override
    public void modifyMyLearningHistory(MyLearningHistoryDTO myLearningHistoryDTO, String username) {
        String type = myLearningHistoryDTO.getType();
        AppUser appUser = userRepository.findByUsernameAndEnabledTrueAndDeletedFalse(username);
        // Nếu type là content (VIDEO, DOCUMENT,...)
        modifyLessonContentUploadHistory(myLearningHistoryDTO, appUser);
        try {
            checkCompletedCourse(myLearningHistoryDTO, appUser);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public InputStream downloadFile(String fileURL) {
        URL url = null;
        InputStream inputStream = null;
        try {
            url = new URL(fileURL);
            inputStream = url.openStream();
        } catch (MalformedURLException e) {
            log.error("ERROR: ", e.getCause().getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            log.error("ERROR: ", e.getCause().getMessage());
            e.printStackTrace();
        }
        return inputStream;
    }


    private void modifyLessonContentUploadHistory(MyLearningHistoryDTO myLearningHistoryDTO, AppUser appUser) {
        Long contentUploadId = myLearningHistoryDTO.getContentId();
        Long lessonStructureId = myLearningHistoryDTO.getLessonStructureId();
        LessonContentUpload lessonContentUpload = lessonContentUploadRepository
                .findLessonContentUploadByLessonStructureIdAndContentUploadId(lessonStructureId, contentUploadId);
        if (lessonContentUpload == null)
            throw new ResourceNotFoundException("Không tồn tại nội dung này!");
        // Nếu tồn tại lesson content upload history -> update
        if (lessonContentUploadHistoryRepository.existsByAppUser_IdAndLessonContentUpload_Id(appUser.getId(), lessonContentUpload.getId())) {
            LessonContentUploadHistory lessonContentUploadHistory = lessonContentUploadHistoryRepository
                    .findByAppUser_IdAndLessonContentUpload_Id(appUser.getId(), lessonContentUpload.getId());
            if (!lessonContentUploadHistory.isCompleted())
                lessonContentUploadHistory.setCompleted(myLearningHistoryDTO.isCompleted());
            lessonContentUploadHistoryRepository.save(lessonContentUploadHistory);
        } else { // Nếu chưa tồn tại lesson content upload history -> create new
            LessonContentUploadHistory lessonContentUploadHistory = new LessonContentUploadHistory();
            lessonContentUploadHistory.setLessonContentUpload(lessonContentUpload);
            lessonContentUploadHistory.setAppUser(appUser);
            lessonContentUploadHistory.setCompleted(myLearningHistoryDTO.isCompleted());
            lessonContentUploadHistoryRepository.save(lessonContentUploadHistory);
        }
    }

    private Long getAppUserIdByUserName(String username) {
        AppUser appUser = userRepository.findByUsernameAndEnabledTrueAndDeletedFalse(username);
        return appUser.getId();
    }

    public void checkCompletedCourse(MyLearningHistoryDTO myLearningHistoryDTO, AppUser appUser) throws Exception {
        Long lessonStructureId = myLearningHistoryDTO.getLessonStructureId();
        Long userId = appUser.getId();
        LessonStructure lessonStructure = lessonStructureRepository.getById(lessonStructureId);


        //check status completed of lesson
        boolean isLessonCompleted = true;
        for (LessonContentUpload lsc : lessonStructure.getLessonContentUploads()) {
            if (lsc.getContentUpload().getType().equals(TypeContentUploadEnum.VIDEO) && !lessonContentUploadHistoryRepository.existsByLessonContentUpload_IdAndAppUser_IdAndIsCompletedTrue(
                    lsc.getId(),
                    userId)) {
                isLessonCompleted = false;
                break;
            }
        }

        if(isLessonCompleted){
            AssignCourse assignCourse = assignCourseRepository.getAssignCourseByAppUser_IdAndContentUploadId(
                    userId,
                    myLearningHistoryDTO.getContentId(),
                    lessonStructureId
            );

            UserLessonProcess userLessonProcess = new UserLessonProcess();
            userLessonProcess.setIsCompleted(true);
            userLessonProcess.setLessonStructure(lessonStructure);
            userLessonProcess.setAssignCourse(assignCourse);

            userLessonProcessRepository.save(userLessonProcess);

            //check status completed of course
            boolean isCourseCompleted = true;
            for(LessonStructure ls : assignCourse.getCourse().getLessonStructures()){
                if(!userLessonProcessRepository.existsUserLessonProcessByAssignCourseIdAndLessonStructureIdAndIsCompletedTrue(
                        assignCourse.getId(), ls.getId()
                )){
                    isCourseCompleted = false;
                    break;
                };
            }
            if(isCourseCompleted){
                assignCourseService.updateCompletedCourse(userId,assignCourse.getCourse().getId());
            }
        }
    }

}
