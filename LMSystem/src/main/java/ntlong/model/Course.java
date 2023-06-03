package ntlong.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Course extends BaseEntity {

    @Column
    private String name;

    @Size(max = 300, message = "Maximum summary length: 300 characters")
    @Column
    private String summary;  // Tổng quan khóa học

    @Column
    @Size(max = 5000, message = "Maximum detail length: 5000 characters")
    private String detail; // Chi tiết khóa học

    @Column
    private String pathOfferBy;

    @Column
    private String fileNameOfferBy;

    @Column
    private String pathProvideBy;

    @Column
    private String fileNameProvideBy;

    @Column
    private String pathPreview;
    @Column
    private String fileNamePreview;// Tên file preview Video/images

    @Column(name = "instructor_name")
    private String instructorName;

    @Column(name = "is_free_course", columnDefinition = "boolean default true")
    private Boolean freeCourse;

    @Column(name = "price")
    private Long price;

    @Column(name = "rate")
    private float rate;

    @Column(name = "is_hot",columnDefinition = "boolean default false")
    private boolean isHot = false;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(joinColumns = {
            @JoinColumn(nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(nullable = false, updatable = false)})
    private Set<Tag> tags;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(joinColumns = {
            @JoinColumn(nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(nullable = false, updatable = false)})
    private Set<RequirementCourse> requirementCourses;


    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "course_skills",
            joinColumns = {
            @JoinColumn(nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(nullable = false, updatable = false)})
    private Set<Skill> skills;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "course_experiences",
            joinColumns = {
                    @JoinColumn(nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(nullable = false, updatable = false)})
    private Set<Experience> experiences;

    @OneToOne(mappedBy = "course",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    private CourseSetting courseSetting;

    @OneToMany(mappedBy = "course", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    private List<LessonStructure> lessonStructures;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_training_id")
    private CategoryTraining categoryTraining; // Hạng mục đào tạo

    @OneToMany(mappedBy = "course", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    private List<RatingCourse> ratingCourses;

}
