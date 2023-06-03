package ntlong.controller;

import lombok.RequiredArgsConstructor;
import ntlong.annotation.CurrentUser;
import ntlong.configuration.PaymentConfig;
import ntlong.enums.PaymentEnum;
import ntlong.model.OrderUser;
import ntlong.payload.request.PaymentRequest;
import ntlong.payload.response.OrderResponse;
import ntlong.repository.OrderRepository;
import ntlong.response.BaseResponse;
import ntlong.service.PaymentService;
import ntlong.utils.CommonImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-payment")
    public ResponseEntity<BaseResponse> createPayment(@RequestBody PaymentRequest paymentRequest,
                                                      HttpServletRequest req,
                                                      @CurrentUser UserDetails userDetails)
            throws IOException {
        String ipAddress = PaymentConfig.getIpAddress(req);
        String username = userDetails.getUsername();
        String paymentUrl = paymentService.createPayment(paymentRequest,username, ipAddress);
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.value(), "Create Payment Successfully",
                paymentUrl), HttpStatus.OK);
    }

    @GetMapping("/save-payment")
    public ResponseEntity<?> savePaymentSuccess(HttpServletRequest request) {
        return paymentService.savePayment(request);
    }
}
