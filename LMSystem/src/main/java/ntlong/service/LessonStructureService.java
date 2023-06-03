package ntlong.service;

import ntlong.dto.LessonStructureDTO;
import ntlong.exception.ResourceNotFoundException;
import ntlong.payload.request.SortCourseStructureRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LessonStructureService {
    Page<LessonStructureDTO> searchLessonStructure(Pageable pageable);

    LessonStructureDTO findById(Long id) throws ResourceNotFoundException;

    LessonStructureDTO createLessonStructure(LessonStructureDTO lessonStructureDTO);

    LessonStructureDTO updateLessonStructure(LessonStructureDTO lessonStructureDTO) throws ResourceNotFoundException;

    void deleteByIds(List<Long> ids) throws ResourceNotFoundException;

    List<LessonStructureDTO> findByCourseId(Long courseId) throws ResourceNotFoundException;

    void swapSortOrderLessonStructure(SortCourseStructureRequest request);
}
