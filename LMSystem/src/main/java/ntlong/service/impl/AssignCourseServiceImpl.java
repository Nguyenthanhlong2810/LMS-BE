package ntlong.service.impl;

import lombok.RequiredArgsConstructor;
import ntlong.common.Util;
import ntlong.dto.AssignCourseDTO;
import ntlong.enums.StatusCourseEnum;
import ntlong.enums.TypeAssignEnum;
import ntlong.exception.CustomException;
import ntlong.exception.ResourceNotFoundException;
import ntlong.model.*;
import ntlong.repository.*;
import ntlong.service.AssignCourseService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignCourseServiceImpl implements AssignCourseService {

    private final AssignCourseRepository assignCourseRepository;

    private final ModelMapper modelMapper;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    private final UserLessonProcessRepository userLessonProcessRepository;

    private final LessonContentUploadRepository lessonContentUploadRepository;

    private final LessonContentUploadHistoryRepository lessonContentUploadHistoryRepository;


    public AssignCourse assignFreeCourseToUser(Course course, Long userId){
        if(!assignCourseRepository.existsAssignCourseByAppUser_IdAndCourse_Id(userId,course.getId())) {
            Optional<AppUser> appUser = userRepository.findByIdAndEnabledTrueAndDeletedFalse(userId);
            if (appUser.isEmpty()) {
                throw new ResourceNotFoundException("User is not found");
            }
            AssignCourse assignCourse = new AssignCourse();
            assignCourse.setAppUser(appUser.get());
            assignCourse.setCourse(course);
            assignCourse.setTypeAssign(TypeAssignEnum.FREE);
            assignCourse.setProgressStatus(StatusCourseEnum.UNCOMPLETED);
            assignCourse.setAssignDate(new Date());
            return assignCourseRepository.save(assignCourse);
        }
        return assignCourseRepository.getAssignCourseByCourseIdAndAppUserIdAndDeletedFalse(course.getId(),userId);
    }
    @Transactional
    @Override
    public AssignCourseDTO updateCompletedCourse(Long userId, Long courseId) throws Exception {
        log.info("updating completed course");
        AssignCourse assignCourse = assignCourseRepository.getDistinctFirstByAppUser_IdAndCourse_Id(userId,
                courseId);
        assignCourse.setProgressStatus(StatusCourseEnum.COMPLETED);
        Date date = new Date();
        assignCourse.setCompletedDate(date);
        assignCourseRepository.save(assignCourse);
        return modelMapper.map(assignCourseRepository.save(assignCourse),AssignCourseDTO.class);
    }

    @Override
    @Transactional
    public void updateLastVisitedCoursePage(Long userId, Long courseId) {
        assignCourseRepository.updateLastVisitedCoursePage(userId, courseId);
    }

    @Override
    public AssignCourseDTO createAssignCourse(String username, Long courseId, TypeAssignEnum assignEnum, StatusCourseEnum status) {
        Optional<Course> course = courseRepository.findByIdAndDeletedFalse(courseId);
        if(course.isEmpty()){
            throw new ResourceNotFoundException("Course is not found");
        }
        AppUser appUser = userRepository.findByUsernameAndEnabledTrueAndDeletedFalse(username);
        if(Objects.isNull(appUser)){
            throw new ResourceNotFoundException("User is not found");
        }
        if(!assignCourseRepository.existsAssignCourseByAppUser_IdAndCourse_Id(appUser.getId(),courseId)) {
            AssignCourse assignCourse = AssignCourse.builder()
                    .course(course.get())
                    .appUser(appUser)
                    .assignDate(new Date())
                    .typeAssign(assignEnum)
                    .progressStatus(status).build();
            return modelMapper.map(assignCourseRepository.save(assignCourse),AssignCourseDTO.class);
        }
        return null;
    }

    @Override
    public AssignCourseDTO getAssignCourse(Long userId, Long courseId) {
        AssignCourse assignCourse = assignCourseRepository.getAssignCourseByCourseIdAndAppUserIdAndDeletedFalse(courseId,userId);
        AssignCourseDTO assignCourseDTO = null;
        if(Objects.nonNull(assignCourse)){
            assignCourseDTO = modelMapper.map(assignCourse,AssignCourseDTO.class);
        }
        return assignCourseDTO;
    }

    @Override
    public Page<AssignCourseDTO> getAllAssignCourse(Integer pageNo, Integer pageSize,
                                                    String courseName, Long courseId,String nameUser, TypeAssignEnum typeAssign) {
        Pageable paging = PageRequest.of(pageNo-1,pageSize);
        Page<AssignCourseDTO> assignCourseList = assignCourseRepository
                .findAllAssignCourses(paging,courseName,courseId,nameUser,typeAssign);
        assignCourseList.stream().forEach(assignCourse -> assignCourse
                        .setAssignDate(Util.getDateWithoutTimeUsingFormat(assignCourse.getAssignDate())));
        return assignCourseList;
    }

    @Override
    public AssignCourseDTO getAssignCourseById(Long assignCourseId) {
        return assignCourseRepository.findAssignCourseById(assignCourseId);
    }

    @Override
    @Transactional
    public void cancelAssignCourse(Long courseId, String username) {
        Long userId = userRepository.findIdByUsername(username);
        AssignCourse assignCourse = assignCourseRepository.getAssignCourseByCourseIdAndAppUserIdAndDeletedFalse(courseId,userId);
        deleteProgressCourse(courseId,userId,assignCourse.getId());
        assignCourseRepository.delete(assignCourse);
    }

    @Override
    @Transactional
    public void resetProgressCourse(Long courseId, String username) {
        Long userId = userRepository.findIdByUsername(username);
        AssignCourse ac = assignCourseRepository.getAssignCourseByCourseIdAndAppUserIdAndDeletedFalse(courseId,userId);
        ac.setProgressStatus(StatusCourseEnum.NOT_STARTED);
        ac.setMyCertificateImageLink(null);
        ac.setMyCertificatePdfLink(null);
        assignCourseRepository.save(ac);
        deleteProgressCourse(courseId,userId,ac.getId());
    }

    private void deleteProgressCourse(Long courseId, Long userId, Long assignCourseId){
        List<Long> lessonContentUploadHistory = lessonContentUploadRepository
                .getListLessonContentUploadIdByCourseId(courseId);
        lessonContentUploadHistoryRepository.deleteAllByAppUserIdAndLessonContentUploadIds(userId,
                lessonContentUploadHistory);
        userLessonProcessRepository.deleteAllByAssignCourseId(assignCourseId);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        assignCourseRepository.deleteByIds(ids);
    }
}
