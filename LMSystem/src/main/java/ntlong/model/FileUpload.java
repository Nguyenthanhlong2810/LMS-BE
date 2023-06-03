package ntlong.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "file_upload")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileUpload extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name="link_file")
    private String linkFile;



}
