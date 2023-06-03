package ntlong.service;

import ntlong.dto.ContentUploadDTO;
import ntlong.exception.BusinessException;
import ntlong.exception.ResourceNotFoundException;
import ntlong.enums.TypeContentUploadEnum;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ContentUploadService {
    List<ContentUploadDTO> createContent(ContentUploadDTO contentUploadDTO, MultipartFile[] files) throws IOException;

    ContentUploadDTO updateContent(ContentUploadDTO contentUploadDTO) throws ResourceNotFoundException;

    void deleteByIds(List<Long> ids) throws BusinessException;

    Page<ContentUploadDTO> searchContenUploads(Integer pageNo, Integer pageSize, String keySearch, TypeContentUploadEnum type, String sortBy);

    ContentUploadDTO findById(Long id) throws ResourceNotFoundException;
}
