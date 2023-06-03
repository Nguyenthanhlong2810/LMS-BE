package ntlong.service;

import ntlong.enums.PaymentEnum;
import ntlong.payload.request.OrderRequest;
import ntlong.payload.response.OrderResponse;

import java.io.UnsupportedEncodingException;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest, String username);
    OrderResponse updateOrder(Long orderId, String responseCode, String message,String bankCode);
}
