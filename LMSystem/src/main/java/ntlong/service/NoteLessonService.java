package ntlong.service;

import ntlong.dto.NoteDTO;
import org.springframework.data.domain.Page;

public interface NoteLessonService {
    NoteDTO createNote(NoteDTO noteDTO, String username);

    NoteDTO updateNote(NoteDTO noteDTO, String username);

    NoteDTO deleteNote(Long id);

    Page<NoteDTO> getNotes(int pageNo, int pageSize, Long contentUploadId, Long courseId, String username, String sortBy);
}
