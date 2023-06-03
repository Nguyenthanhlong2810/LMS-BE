package ntlong.service.impl;

import ntlong.converter.LessonStructureConverter;
import ntlong.dto.LessonStructureDTO;
import ntlong.exception.ResourceNotFoundException;
import ntlong.model.LessonContentUpload;
import ntlong.model.LessonStructure;
import ntlong.payload.request.IdValuePair;
import ntlong.payload.request.SortCourseStructureRequest;
import ntlong.repository.LessonContentUploadRepository;
import ntlong.repository.LessonStructureRepository;
import ntlong.service.LessonStructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonStructureServiceImpl implements LessonStructureService {
    private static final String LESSON_TYPE = "lesson";
    private static final String CONTENT_UPLOAD_TYPE = "content";
    private final LessonStructureRepository lessonStructureRepository;
    private final LessonStructureConverter lessonStructureConverter;

    private final LessonContentUploadRepository lessonContentUploadRepository;

    @Override
    public Page<LessonStructureDTO> searchLessonStructure(Pageable pageable) {
        Page<LessonStructure> lessonStructurePages = lessonStructureRepository.findAll(pageable);
        return lessonStructurePages.map(lessonStructureConverter::convertToDTO);
    }

    @Override
    public LessonStructureDTO findById(Long id) throws ResourceNotFoundException {
        LessonStructure lessonStructure = lessonStructureRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("LessonStructure does not existed"));
        return lessonStructureConverter.convertToDTO(lessonStructure);
    }

    @Override
    @Transactional
    public LessonStructureDTO createLessonStructure(LessonStructureDTO lessonStructureDTO) {
        LessonStructure lessonStructure = lessonStructureConverter.convertToEntity(lessonStructureDTO);
        lessonStructure.setDeleted(Boolean.FALSE);
        lessonStructure.setVersion(1);
        lessonStructure = lessonStructureRepository.saveAndFlush(lessonStructure);
        return lessonStructureConverter.convertToDTO(lessonStructure);
    }

    @Override
    @Transactional
    public LessonStructureDTO updateLessonStructure(LessonStructureDTO lessonStructureDTO) throws ResourceNotFoundException {
        if (Objects.isNull(lessonStructureDTO.getId())) throw new ResourceNotFoundException("Id null for update");
        if(!lessonStructureRepository.existsById(lessonStructureDTO.getId())) throw new ResourceNotFoundException("LessonStructure does not existed");

        LessonStructure lessonStructureConverted = lessonStructureConverter.convertToEntity(lessonStructureDTO);
        return  lessonStructureConverter.convertToDTO(lessonStructureRepository.save(lessonStructureConverted));
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) throws ResourceNotFoundException {
        boolean check = ids.stream().anyMatch(id -> !lessonStructureRepository.existsById(id));
        if (check) throw new ResourceNotFoundException("LessonStructure does not existed");
        lessonStructureRepository.deleteAllById(ids);
    }

    @Override
    public List<LessonStructureDTO> findByCourseId(Long courseId) throws ResourceNotFoundException {
        List<LessonStructure> lessonStructures = lessonStructureRepository.findByCourseId(courseId);
        return lessonStructures.stream().map(lessonStructureConverter::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public void swapSortOrderLessonStructure(SortCourseStructureRequest request) {
        List<Long> idReqs = request.getChildIds().stream().map(IdValuePair::getId).collect(Collectors.toList());

        boolean isSortLesson = request.getChildIds().stream().anyMatch(child -> Objects.equals(child.getType(), LESSON_TYPE));
        if (isSortLesson) {
            List<LessonStructure> lessonStructures = lessonStructureRepository.findByCourseId(request.getParentId())
                    .stream()
                    .filter(item->idReqs.contains(item.getId()))
                    .collect(Collectors.toList());

            for (IdValuePair idValuePair : request.getChildIds()) {
                for (LessonStructure lessonStructure : lessonStructures) {
                    if (lessonStructure.getId().equals(idValuePair.getId())) {
                        lessonStructure.setSortOrder(idValuePair.getSortOrder());
                    }
                }
            }
            lessonStructureRepository.saveAllAndFlush(lessonStructures);
        } else {
            for (IdValuePair idValuePair : request.getChildIds()) {
                if (Objects.equals(idValuePair.getType(), CONTENT_UPLOAD_TYPE)) {
                    // Update sort order by setting in CONTENT UPLOAD
                    LessonContentUpload lessonContentUpload = lessonContentUploadRepository.findLessonContentUploadByLessonStructureIdAndContentUploadId(request.getParentId(), idValuePair.getId());
                    lessonContentUpload.setSortOrder(idValuePair.getSortOrder());
                    lessonContentUploadRepository.save(lessonContentUpload);
                }
            }
        }
    }

}