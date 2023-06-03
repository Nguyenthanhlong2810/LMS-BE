package ntlong.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileDTO {

    private String fileName;
    private String previewUrl;
    private String message;

    public FileDTO(String message) {
        this.message = message;
    }

    public FileDTO(String fileName, String previewUrl) {
        this.fileName = fileName;
        this.previewUrl = previewUrl;
    }

    public FileDTO() {
    }
}
