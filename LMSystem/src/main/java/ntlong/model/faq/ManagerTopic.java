package ntlong.model.faq;

import ntlong.enums.StatusFAQEnum;
import ntlong.model.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * Created by txtrung
 * Date: 06/06/2022
 * Time: 15:49
 * Project name: lms
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "manager_topic")
public class ManagerTopic extends BaseEntity {

    @Column
    private String code;

    @Column
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    private StatusFAQEnum statusTopic;

    @OneToMany(mappedBy = "managerTopic", fetch = FetchType.LAZY)
    @OrderBy("createdDate")
    private List<ManagerQuestion> managerQuestions;

}
