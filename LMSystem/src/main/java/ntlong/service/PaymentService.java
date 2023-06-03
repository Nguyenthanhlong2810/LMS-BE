package ntlong.service;

import ntlong.payload.request.PaymentRequest;
import ntlong.payload.response.OrderResponse;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

public interface PaymentService {

    String createPayment(PaymentRequest paymentRequest,String username, String ipAddress) throws UnsupportedEncodingException;

    ResponseEntity<?> savePayment(HttpServletRequest request);
}
