package ntlong.model.faq;

import ntlong.enums.StatusFAQEnum;
import ntlong.model.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by txtrung
 * Date: 07/06/2022
 * Time: 10:52
 * Project name: lms-faq
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "manager_question")
public class ManagerQuestion extends BaseEntity {

    @Column
    private String code;

    @Column
    private String question;

    @Column
    private String answer;

    @Column
    @Enumerated(EnumType.STRING)
    private StatusFAQEnum statusQuestion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manager_topic_id", nullable = false)
    private ManagerTopic managerTopic;
}
