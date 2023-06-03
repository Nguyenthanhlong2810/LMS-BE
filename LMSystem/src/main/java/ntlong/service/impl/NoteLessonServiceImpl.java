package ntlong.service.impl;

import ntlong.dto.NoteDTO;
import ntlong.exception.ResourceNotFoundException;
import ntlong.model.*;
import ntlong.repository.*;
import ntlong.service.NoteLessonService;
import ntlong.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NoteLessonServiceImpl implements NoteLessonService {

    private final NoteLessonRepository noteLessonRepository;
    private final ModelMapper modelMapper;

    private final ContentUploadRepository contentUploadRepository;

    private final AssignCourseRepository assignCourseRepository;

    private final LessonStructureRepository lessonStructureRepository;

    private final UserService userService;

    private final UserRepository userRepository;

    private final LessonContentUploadRepository lessonContentUploadRepository;

    /**
     * Create Note
     * @param noteDTO
     * @param username
     * @return created note
     */
    @Override
    public NoteDTO createNote(NoteDTO noteDTO, String username) {
        AppUser appUser = userService.search(username);
        LessonContentUpload lessonContentUpload = lessonContentUploadRepository.findById(noteDTO.getLessonContentUploadId())
                .orElseThrow(()-> new ResourceNotFoundException("LessonContentUpload with id = "+noteDTO.getLessonContentUploadId()+" is not found"));
        Long contentUploadId = lessonContentUpload.getContentUpload().getId();
        Long lessonStructureId = lessonContentUpload.getLessonStructure().getId();
        AssignCourse assignCourse = assignCourseRepository.getAssignCourseByAppUser_IdAndContentUploadId(appUser.getId(),
                contentUploadId,
                lessonStructureId);
        if (Objects.isNull(assignCourse)) {
            throw new ResourceNotFoundException("User is not assigned to this lesson or this content with id "
                    + noteDTO.getContentUploadId() + " is not a video");
        }
        NoteLesson noteLesson = modelMapper.map(noteDTO, NoteLesson.class);
        ContentUpload contentUpload = contentUploadRepository.getById(contentUploadId);
        LessonStructure lessonStructure = lessonStructureRepository.getById(lessonStructureId);
        noteLesson.setAppUser(appUser);
        noteLesson.setContentUpload(contentUpload);
        noteLesson.setLessonStructure(lessonStructure);
        NoteDTO createdNote = modelMapper.map(noteLessonRepository.save(noteLesson), NoteDTO.class);
        createdNote.setContentUploadId(contentUploadId);
        createdNote.setLessonStructureId(lessonStructureId);
        createdNote.setLessonContentUploadId(noteDTO.getLessonContentUploadId());
        return createdNote;
    }

    /**
     * Update note
     * @param noteDTO
     * @param username
     * @return updated note
     */
    @Override
    public NoteDTO updateNote(NoteDTO noteDTO, String username) {
        Long userId = userRepository.findIdByUsername(username);
        NoteLesson noteLesson = noteLessonRepository.getById(noteDTO.getId());
        AssignCourse assignCourse = assignCourseRepository.getAssignCourseByAppUser_IdAndContentUploadId(userId,
                noteLesson.getContentUpload().getId(),
                noteLesson.getLessonStructure().getId());
        if (Objects.isNull(assignCourse)) {
            throw new ResourceNotFoundException("User is not assigned to this lesson or this content with id "
                    + noteLesson.getContentUpload().getId() + " is not a video");
        }
        noteLesson.setContent(noteDTO.getContent());
        NoteDTO updatedNote = modelMapper.map(noteLessonRepository.save(noteLesson), NoteDTO.class);
        updatedNote.setContentUploadId(noteLesson.getContentUpload().getId());
        updatedNote.setLessonStructureId(noteLesson.getLessonStructure().getId());
        return updatedNote;
    }

    /**
     * delete note by id
     * @param id
     * @return deleted note
     */
    @Override
    public NoteDTO deleteNote(Long id) {
        NoteLesson deleteNote = noteLessonRepository.getById(id);
        noteLessonRepository.deleteById(id);
        return modelMapper.map(deleteNote, NoteDTO.class);
    }

    /**
     * filter note by content upload id or course id
     * @param pageNo
     * @param pageSize
     * @param lessonContentUploadId
     * @param courseId
     * @param username
     * @return page note
     */
    @Override
    public Page<NoteDTO> getNotes(int pageNo, int pageSize, Long lessonContentUploadId, Long courseId, String username, String sortBy) {
        Pageable paging = PageRequest.of(pageNo - 1, pageSize,  Sort.by(Sort.Order.desc(sortBy)));
        Long userId = userRepository.findIdByUsername(username);
        Page<NoteDTO> noteLessons = null;
        if (Objects.nonNull(lessonContentUploadId)) {
            LessonContentUpload lessonContentUpload = lessonContentUploadRepository.findById(lessonContentUploadId)
                    .orElseThrow(()-> new ResourceNotFoundException("LessonContentUpload with id = "+lessonContentUploadId+" is not found"));
            noteLessons = noteLessonRepository.getNoteListByContentUpload(paging, lessonContentUpload.getContentUpload().getId(),
                    lessonContentUpload.getLessonStructure().getId(), userId);
        } else if (Objects.nonNull(courseId)) {
            noteLessons = noteLessonRepository.getNoteListByCourse(paging, courseId, userId);
        }
        for (NoteDTO note : noteLessons.getContent()) {
            note.setLessonName(lessonStructureRepository.getLessonStructureNameByContentUploadIdAndLessonStructureId(note.getContentUploadId(),
                    note.getLessonStructureId()));
        }
        return noteLessons;
    }
}
