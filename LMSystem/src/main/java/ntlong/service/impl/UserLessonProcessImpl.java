package ntlong.service.impl;

import ntlong.dto.lessonprocess.UserLessonProcessDTO;
import ntlong.exception.ResourceNotFoundException;
import ntlong.model.AssignCourse;
import ntlong.model.LessonStructure;
import ntlong.model.UserLessonProcess;
import ntlong.payload.response.lesson.LessonProcessResponse;
import ntlong.repository.AssignCourseRepository;
import ntlong.repository.LessonStructureRepository;
import ntlong.repository.UserLessonProcessRepository;
import ntlong.service.UserLessonProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserLessonProcessImpl implements UserLessonProcessService {

    private final LessonStructureRepository lessonStructureRepository;

    private final AssignCourseRepository assignCourseRepository;

    private final UserLessonProcessRepository lessonProcessRepository;

    @Override
    public LessonProcessResponse createLessonProcess(UserLessonProcessDTO lessonProcessDTO) {
        if(lessonProcessRepository.existsUserLessonProcessByAssignCourse_IdAndLessonStructure_Id(lessonProcessDTO.getAssignCourseId(),
                                                                                                 lessonProcessDTO.getLessonStructureId())){
            throw new EntityExistsException("This lesson process with assign course" +lessonProcessDTO.getAssignCourseId()+
                    " and lesson with "+lessonProcessDTO.getLessonStructureId() + " already existed");
        }
        UserLessonProcess userLessonProcess = new UserLessonProcess();
        if(Objects.nonNull(lessonProcessDTO.getAssignCourseId())){
            AssignCourse assignCourse = assignCourseRepository.findById(lessonProcessDTO.getAssignCourseId())
                    .orElseThrow(()-> new ResourceNotFoundException("assign course does not existed"));
            userLessonProcess.setAssignCourse(assignCourse);
            if(Objects.nonNull(lessonProcessDTO.getLessonStructureId())){
                LessonStructure lessonStructure = lessonStructureRepository.findById(lessonProcessDTO.getLessonStructureId())
                        .orElseThrow(()-> new ResourceNotFoundException("lesson structure does not existed"));
                if(assignCourse.getCourse().getLessonStructures().contains(lessonStructure)) {
                    userLessonProcess.setLessonStructure(lessonStructure);
                }else{
                    throw new ResourceNotFoundException("This course with id" +lessonProcessDTO.getAssignCourseId()+
                            " does contain lesson with id "+lessonProcessDTO.getLessonStructureId());
                }
            }
        }
        userLessonProcess.setIsCompleted(lessonProcessDTO.getIsCompleted());
        UserLessonProcess savedLessonProcess = lessonProcessRepository.save(userLessonProcess);
        return new LessonProcessResponse(savedLessonProcess.getAssignCourse().getId(),
                savedLessonProcess.getLessonStructure().getId(),savedLessonProcess.getIsCompleted());
    }

    @Override
    public LessonProcessResponse updateLessonProcess(UserLessonProcessDTO lessonProcess) {
        UserLessonProcess userLessonProcess = lessonProcessRepository.getUserLessonProcessByAssignCourse_IdAndLessonStructure_Id(
                                                                                        lessonProcess.getAssignCourseId(),
                                                                                        lessonProcess.getLessonStructureId());
        if(userLessonProcess != null){
            userLessonProcess.setIsCompleted(lessonProcess.getIsCompleted());
        }else{
            throw new ResourceNotFoundException("This course with id" +lessonProcess.getAssignCourseId()+
                    " does contain lesson with id "+lessonProcess.getLessonStructureId());
        }
        UserLessonProcess updatedLessonProcess = lessonProcessRepository.save(userLessonProcess);

        return new LessonProcessResponse(updatedLessonProcess.getAssignCourse().getId(),
                updatedLessonProcess.getLessonStructure().getId(),updatedLessonProcess.getIsCompleted());
    }
}
