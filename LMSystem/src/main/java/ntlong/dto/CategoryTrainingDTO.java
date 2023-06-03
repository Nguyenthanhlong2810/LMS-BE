package ntlong.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryTrainingDTO {
    private Long id;
    @Size(max = 50, message = "Maximum name length: 50 characters")
    @Pattern(regexp = "^[^<>%!@#$0-9]*$", message = "Name is not allowed to contain special characters and numbers ")
    private String name;
    private long parent;
    private int no;
    private String description;
    private String title;
}
