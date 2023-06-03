package ntlong.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadFileDTO {

    private String message;
    private int totalFiles;
    private List<FileDTO> files;

    public UploadFileDTO() {
    }

    public UploadFileDTO(String message) {
        this.message = message;
    }

    public UploadFileDTO(int length, List<FileDTO> fileDTOList) {
        this.totalFiles = length;
        this.files = fileDTOList;
    }
}
