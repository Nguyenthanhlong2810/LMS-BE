package ntlong.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "lms_news")
public class LmsNews extends BaseEntity implements Serializable {

    @Column(name = "subject")
    @Size(max = 200, message = "The length of subject can not exceed 200 characters")
    private String subject;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(joinColumns = {
            @JoinColumn(nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(nullable = false, updatable = false)})
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<LmsNewsLabel> lmsNewsLabels = new HashSet<>();

    @Column(name = "text_content")
    @Size(max = 5000, message = "The length of content can not exceed 5000 characters")
    private String textContent;

    @Column(name = "content_type")
    private NewsContentType contentType;

    @Column(name = "status")
    private Boolean status; // Trạng thái của tin tức ( true -> đã đăng, ngược lại false -> bản nháp)

    @Column(name = "is_hot_news")
    private Boolean isHotNews;

    @Column(name = "is_pinned")
    private Boolean isPinned; // Trạng thái tin đã được ghim hay chưa

    @Column(name = "attachment_link")
    private String attachmentLink;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "course_link")
    private Integer courseLink;

    @Column(name = "event_link")
    private Integer eventLink;

}
