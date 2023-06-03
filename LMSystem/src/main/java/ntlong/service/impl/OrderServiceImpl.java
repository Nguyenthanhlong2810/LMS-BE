package ntlong.service.impl;

import lombok.RequiredArgsConstructor;
import ntlong.enums.PaymentEnum;
import ntlong.enums.StatusCourseEnum;
import ntlong.enums.TypeAssignEnum;
import ntlong.model.AppUser;
import ntlong.model.Course;
import ntlong.model.OrderUser;
import ntlong.payload.request.OrderRequest;
import ntlong.payload.response.OrderResponse;
import ntlong.repository.CourseRepository;
import ntlong.repository.OrderRepository;
import ntlong.repository.UserRepository;
import ntlong.service.AssignCourseService;
import ntlong.service.CartService;
import ntlong.service.OrderService;
import ntlong.service.PaymentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;

    private final CourseRepository courseRepository;

    private final OrderRepository orderRepository;

    private final ModelMapper mapper;

    private final AssignCourseService assignCourseService;

    private final CartService cartService;
    @Override
    public OrderResponse createOrder(OrderRequest orderRequest, String username){
        AppUser user = userRepository.findByUsernameAndEnabledTrueAndDeletedFalse(username);
        List<Course> courses = courseRepository.findCoursesByIds(orderRequest.getCourseIDs());

        OrderUser order = OrderUser.builder()
                .statusPaid(orderRequest.getStatusPaid())
                .totalMoney(orderRequest.getTotalMoney())
                .bankCode(orderRequest.getBankCode())
                .courses(courses)
                .user(user)
                .build();

        OrderUser savedOrder = orderRepository.save(order);
        return mapper.map(savedOrder, OrderResponse.class);
    }

    @Transactional
    public OrderResponse updateOrder(Long orderId,String vnpResponseCode,String message,String bankCode) {
        OrderUser orderUser = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order is not found"));

        if(vnpResponseCode.equals("00")) {
            orderUser.setStatusPaid(PaymentEnum.PAY_SUCCESS);
        }else{
            orderUser.setStatusPaid(PaymentEnum.PAY_ERROR);
        }
        orderUser.setMessage_payment(message);
        orderUser.setResponse_code(vnpResponseCode);
        orderUser.setBankCode(bankCode);
        orderRepository.save(orderUser);
        List<Course> courses = orderUser.getCourses();
        List<Long> courseIds = new ArrayList<>();
        for(Course c : courses) {
            assignCourseService
                    .createAssignCourse(orderUser.getUser().getUsername(),c.getId(), TypeAssignEnum.PAID, StatusCourseEnum.NOT_STARTED);
            courseIds.add(c.getId());
        }
        cartService.removeProducts(courseIds,orderUser.getUser().getUsername());
        return mapper.map(orderUser,OrderResponse.class);
    }
}
