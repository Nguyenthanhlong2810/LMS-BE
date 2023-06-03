package ntlong.payload.response;

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
public class OrderResponse {

    private Long id;

    private PaymentEnum statusPaid;

    private int totalMoney;

    private List<Long> courseIDs;
}
