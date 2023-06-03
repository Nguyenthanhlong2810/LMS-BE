package ntlong.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ntlong.enums.PaymentEnum;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    private String email;

    private String fullName;

    private String phoneNumber;

    private String address;

    private String country;

    private String city;

    private String bankCode;

    private PaymentEnum statusPaid;

    private int totalMoney;

    private List<Long> courseIDs;

}
