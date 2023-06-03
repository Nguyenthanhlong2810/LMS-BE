package ntlong.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "token")
@Builder
public class VerificationToken extends BaseEntity {
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    private AppUser appUser;

    private Timestamp expiryDate;
}
