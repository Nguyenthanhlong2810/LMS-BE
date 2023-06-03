package ntlong.model;

import lombok.*;
import ntlong.enums.PaymentEnum;

import javax.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
@Builder
@Table(name = "order_user")
@NoArgsConstructor
@AllArgsConstructor
public class OrderUser extends BaseEntity{

    @Column(name = "response_code")
    private String response_code;

    @Column(name = "message_payment")
    private String message_payment;

    @Column(name = "status_paid")
    @Enumerated(EnumType.STRING)
    private PaymentEnum statusPaid;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "total_money")
    private int totalMoney;

    @Column(name = "transaction_id")
    private String transactionId;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "order_courses",
            joinColumns = {
                    @JoinColumn(nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(nullable = false, updatable = false)})
    private List<Course> courses;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user; // Khóa học

}
