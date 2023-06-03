package ntlong.service.impl;

import lombok.RequiredArgsConstructor;
import ntlong.configuration.PaymentConfig;
import ntlong.enums.PaymentEnum;
import ntlong.exception.CustomException;
import ntlong.model.AppUser;
import ntlong.model.Course;
import ntlong.model.OrderUser;
import ntlong.payload.request.PaymentRequest;
import ntlong.payload.response.OrderResponse;
import ntlong.repository.CourseRepository;
import ntlong.repository.OrderRepository;
import ntlong.repository.UserRepository;
import ntlong.response.BaseResponse;
import ntlong.service.OrderService;
import ntlong.service.PaymentService;
import ntlong.utils.CommonImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final OrderRepository orderRepository;

    private final OrderService orderService;

    private final CommonImpl common;

    private final UserRepository userRepository;

    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public String createPayment(PaymentRequest paymentRequest,String username, String ipAddress) throws UnsupportedEncodingException {
        Long userId = userRepository.findIdByUsername(username);
        String transId = common.generateTransactionId(userId);

        AppUser appUser = userRepository.findByUsernameAndEnabledTrueAndDeletedFalse(username);
        List<Course> courses = courseRepository.findCoursesByIds(paymentRequest.getCourseIds());
        OrderUser orderUser = new OrderUser();
        orderUser.setTransactionId(transId);
        orderUser.setUser(appUser);
        orderUser.setCourses(courses);
        orderUser.setStatusPaid(PaymentEnum.UNPAID);
        orderUser.setTotalMoney(paymentRequest.getTotalMoney());
        orderRepository.save(orderUser);

        String orderInfo = "Create payment request for user "+ userId;
        //config
        String vnp_Version = PaymentConfig.vnp_Version;
        String vnp_Command = PaymentConfig.vnp_Command;
        String orderType = PaymentConfig.vnp_OrderType;
        String vnp_TmnCode = PaymentConfig.vnp_TmnCode;

        //request
        String vnp_TxnRef = String.valueOf(transId);


        int amount = paymentRequest.getTotalMoney() * 100;
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", PaymentConfig.vnp_CurCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", PaymentConfig.vnp_Locale);

        vnp_Params.put("vnp_ReturnUrl", PaymentConfig.vnp_Returnurl);
        vnp_Params.put("vnp_IpAddr", ipAddress);
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        //Build data to hash and querystring
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = PaymentConfig.hmacSHA512(PaymentConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        return PaymentConfig.vnp_PayUrl + "?" + queryUrl;

    }

    @Override
    public ResponseEntity<?> savePayment(HttpServletRequest request) {
        String messagePayment = null;
        String responseCode = null;
        OrderUser order = null;
            Map<String,String> fields = new HashMap<>();
            for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
                String fieldName = params.nextElement();
                String fieldValue = request.getParameter(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    fields.put(fieldName, fieldValue);
                }
            }
        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType"))
        {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash"))
        {
            fields.remove("vnp_SecureHash");
        }

        String bankCode = fields.get("vnp_BankCode");

        // Check checksum
            //String signValue = PaymentConfig.hashAllFields(fields);
            //if (signValue.equals(vnp_SecureHash)) {
                Optional<OrderUser> optionalOrder = orderRepository.findOrderUserByTransactionId(fields.get("vnp_TxnRef"));

                boolean checkOrderId = optionalOrder.isPresent(); // vnp_TxnRef exists in your database
                boolean checkAmount = true;
                boolean checkOrderStatus = true; // PaymnentStatus = 0 (pending)
                if(checkOrderId) {
                    order = optionalOrder.get();
                    Integer totalMoney = order.getTotalMoney() * 100;
                    // vnp_Amount is valid (Check vnp_Amount VNPAY returns compared to the amount of the code (vnp_TxnRef) in the Your database).
                    if (!totalMoney.equals(Integer.parseInt(fields.get("vnp_Amount")))) {
                        checkAmount = false;
                    }
                    if(checkAmount) {
                        if (!order.getStatusPaid().equals(PaymentEnum.UNPAID)) {
                            checkOrderStatus = false;
                        }
                    }
                }

                if (checkOrderId) {
                    if (checkAmount) {
                        if (checkOrderStatus) {
                            if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
                                messagePayment = "Confirm Success";
                                responseCode  = "00";
                            }
                            System.out.println("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
                        } else {
                            messagePayment = "Order already confirmed";
                            responseCode  = "02";
                            System.out.println("{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}");
                        }
                    } else {
                        messagePayment = "Invalid Amount";
                        responseCode  = "04";
                        System.out.println("{\"RspCode\":\"04\",\"Message\":\"Invalid Amount\"}");
                    }
                } else {
                    messagePayment = "Order not Found";
                    responseCode  = "01";
                    System.out.println("{\"RspCode\":\"01\",\"Message\":\"Order not Found\"}");
                }
//            } else {
//                messagePayment = "Invalid Checksum";
//                responseCode  = "97";
//                System.out.println("{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}");
//            }
        if(optionalOrder.isPresent()){
            orderService.updateOrder(order.getId(),responseCode,messagePayment,bankCode);
        }

        return new ResponseEntity<>(messagePayment, HttpStatus.OK);

    }
}
