package ntlong.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Cart extends BaseEntity {
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "cart_course",
            joinColumns = {
                    @JoinColumn(nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(nullable = false, updatable = false)})
    private List<Course> courses;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotFound(action= NotFoundAction.IGNORE)
    @JsonIgnore
    private AppUser appUser;
}
