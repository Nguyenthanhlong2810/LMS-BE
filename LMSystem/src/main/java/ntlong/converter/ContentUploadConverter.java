package ntlong.converter;


import ntlong.dto.ContentUploadDTO;
import ntlong.model.ContentUpload;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContentUploadConverter {

    private final ModelMapper modelMapper;

    public ContentUploadDTO convertToDTO(ContentUpload contentUpload) {
        ContentUploadDTO contentUploadDTO = modelMapper.map(contentUpload, ContentUploadDTO.class);
        return contentUploadDTO;
    }

    public ContentUpload convertToEntity(ContentUploadDTO contentUploadDTO) {
        ContentUpload contentUpload = modelMapper.map(contentUploadDTO, ContentUpload.class);
        return contentUpload;
    }
}
