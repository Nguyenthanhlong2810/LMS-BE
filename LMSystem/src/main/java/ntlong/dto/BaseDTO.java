package ntlong.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class BaseDTO {
    protected Long id;

    protected Boolean status;

    protected String description;
}
