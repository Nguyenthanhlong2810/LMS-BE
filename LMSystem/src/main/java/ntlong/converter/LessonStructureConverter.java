package ntlong.converter;


import ntlong.dto.ContentUploadDTO;
import ntlong.dto.LessonStructureDTO;
import ntlong.enums.TypeContentUploadEnum;
import ntlong.exception.ResourceNotFoundException;
import ntlong.model.ContentUpload;
import ntlong.model.Course;
import ntlong.model.LessonContentUpload;
import ntlong.model.LessonStructure;
import ntlong.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LessonStructureConverter {

    private final ContentUploadRepository contentUploadRepository;
    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;
    private final ContentUploadConverter contentUploadConverter;
    private final LessonContentUploadRepository lessonContentUploadRepository;


    public LessonStructureDTO convertToDTO(LessonStructure lessonStructure) {
        LessonStructureDTO lessonStructureDTO = modelMapper.map(lessonStructure, LessonStructureDTO.class);
        lessonStructureDTO.setCourseId(lessonStructure.getCourse().getId());
        if (!CollectionUtils.isEmpty(lessonStructure.getLessonContentUploads())) {
            lessonStructureDTO.setContentUploadIds(lessonStructure.getLessonContentUploads().stream().map(item -> item.getContentUpload().getId()).collect(Collectors.toList()));
            lessonStructureDTO.setContentUploads(lessonStructure.getLessonContentUploads().stream().map(item -> {
                ContentUploadDTO contentUploadDTO = contentUploadConverter.convertToDTO(item.getContentUpload());
                contentUploadDTO.setCanDownload(item.isCanDownload());
                contentUploadDTO.setCompletedOpen(item.isCompletedOpen());
                contentUploadDTO.setSortOrder(item.getSortOrder());
                contentUploadDTO.setConditionPass(item.getConditionPass());
                contentUploadDTO.setDuration(Math.round((float) item.getDuration())); // trả về số giây
                contentUploadDTO.setLinkFileContent(contentUploadDTO.getLinkFileContent());
                contentUploadDTO.setLessonContentUploadId(item.getId());
                return contentUploadDTO;
            }).sorted(Comparator.comparingLong(ContentUploadDTO::getSortOrder)).collect(Collectors.toList()));
        }

        return lessonStructureDTO;
    }

    public LessonStructure convertToEntity(LessonStructureDTO lessonStructureDTO) {
        LessonStructure lessonStructure = modelMapper.map(lessonStructureDTO, LessonStructure.class);
        List<Long> contentUploadIds = lessonStructureDTO.getContentUploadIds();

        if (Objects.nonNull(lessonStructureDTO.getCourseId())) {
            if (!courseRepository.existsById(lessonStructureDTO.getCourseId()))
                throw new ResourceNotFoundException("Course does not existed");
            Course course = new Course();
            course.setId(lessonStructureDTO.getCourseId());
            lessonStructure.setCourse(course);
        }

        boolean isUpdateCase = lessonStructureDTO.getId() != null && lessonStructureDTO.getId() != 0;
        if (isUpdateCase) {
            if (!CollectionUtils.isEmpty(lessonStructureDTO.getContentUploads())) {
                List<LessonContentUpload> lessonContentUploads = lessonContentUploadRepository.findLessonContentUploadsByLessonStructureId(lessonStructureDTO.getId());

                List<ContentUploadDTO> contentUploadDTOS = lessonStructureDTO.getContentUploads();

                lessonContentUploads.forEach(lessonContentUpload -> {
                    contentUploadDTOS.forEach(contentUploadDTO -> {
                        Long lessConUpload = lessonContentUpload.getContentUpload().getId();
                        if (Objects.equals(contentUploadDTO.getId(), lessConUpload)) {
                            ContentUpload contentUpload = contentUploadRepository.findById(contentUploadDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("Content Upload does not existed"));
                            lessonContentUpload.setContentUpload(contentUpload);
                            lessonContentUpload.setCanDownload(contentUploadDTO.isCanDownload());
                            lessonContentUpload.setCompletedOpen(contentUploadDTO.isCompletedOpen());
                        }
                    });
                });
                lessonStructure.setLessonContentUploads(lessonContentUploads);
            } else if (!CollectionUtils.isEmpty(lessonStructureDTO.getContentUploadIds())) {
                lessonContentUploadRepository.deleteAllByLessonStructureId(lessonStructureDTO.getId());
                lessonStructure.setLessonContentUploads(processContentUploadCaseInsertToEntity(lessonStructure, contentUploadIds));
            }
        } else if (!CollectionUtils.isEmpty(contentUploadIds)) {
            lessonStructure.setLessonContentUploads(processContentUploadCaseInsertToEntity(lessonStructure, contentUploadIds));
        }
        return lessonStructure;
    }

    private List<LessonContentUpload> processContentUploadCaseInsertToEntity(LessonStructure lessonStructure,List<Long> contentUploadIds){
        List<ContentUpload> contentUploads = contentUploadRepository.findAllById(contentUploadIds);
        List<LessonContentUpload> lessonContentUploads = new ArrayList<>();
        contentUploads.forEach(item -> {
            LessonContentUpload lessonContentUpload = new LessonContentUpload();
            lessonContentUpload.setContentUpload(item);
            lessonContentUpload.setLessonStructure(lessonStructure);
            Long duration  = TypeContentUploadEnum.VIDEO.equals(item.getType()) ? convertTimeHMSStringToLong(item.getTimeLong()) : 0L;
            lessonContentUpload.setDuration(duration);
            lessonContentUploads.add(lessonContentUpload);
        });
        return lessonContentUploads;
    }

    private Long convertTimeHMSStringToLong(String timeString) {
        Long timeLong= 0L, h = 0L, m = 0L, s = 0L;

        if(timeString.indexOf('h') >= 0) {
            h = Long.parseLong(timeString.substring(0, timeString.indexOf('h')));
            if (timeString.indexOf('m') >= 0) {
                m = Long.parseLong(timeString.substring(timeString.indexOf('h') + 1, timeString.indexOf('m')));
                if(timeString.indexOf('s') >= 0) {
                    s = Long.parseLong(timeString.substring(timeString.indexOf('m') + 1, timeString.indexOf('s')));
                }
            }
        } else if(timeString.indexOf('m') >= 0) {
            m = Long.parseLong(timeString.substring(0, timeString.indexOf('m')));
            if(timeString.indexOf('s') >= 0){
                s = Long.parseLong(timeString.substring(timeString.indexOf('m') + 1, timeString.indexOf('s')));
            }
        } else {
            s = Long.parseLong(timeString.substring(0,timeString.indexOf('s')));
        }
        timeLong = s + m * 60 + h * 3600;
        return timeLong;
    }
}
