package ntlong.payload.response.faq;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by txtrung
 * Date: 14/06/2022
 * Time: 11:05
 * Project name: lms-faq
 */

@Getter
@Setter
@NoArgsConstructor
public class ResponseDTO {

    private String method;
    private String mess;

    public ResponseDTO(String method, String mess) {
        this.method = method;
        this.mess = mess;
    }
}
