package ntlong.service.impl;

import lombok.AllArgsConstructor;
import ntlong.dto.CartDTO;
import ntlong.dto.CourseResponse;
import ntlong.enums.RateStar;
import ntlong.exception.CustomException;
import ntlong.exception.ResourceNotFoundException;
import ntlong.model.AppUser;
import ntlong.model.Cart;
import ntlong.model.Course;
import ntlong.repository.*;
import ntlong.service.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {
    private final UserRepository userService;
    private final CourseRepository courseRepository;

    private final CartRepository cartRepository;

    private final ModelMapper mapper;

    private final RatingCourseRepository ratingRepository;

    private final AssignCourseRepository assignCourseRepository;


    @Override
    public void addProduct(Long courseID, String username) {
        Course course = courseRepository.findByIdAndDeletedFalse(courseID)
                .orElseThrow(() -> new ResourceNotFoundException("Course is not found"));
        Long userId = userService.findIdByUsername(username);

        if(assignCourseRepository.checkAssignedCourse(courseID,userId)){
            throw new CustomException("Bạn đã mua khóa học này", HttpStatus.BAD_REQUEST);
        }
        Cart cart = getCartUser(username);
        List<Course> courses = cart.getCourses();
        if (!courses.contains(course)) {
            courses.add(course);
        }
        cartRepository.save(cart);
    }

    @Override
    public void removeProducts(List<Long> courseIDs, String username) {
        Cart cart = getCartUser(username);
        List<Course> courses = cart.getCourses();
        for (Long courseID : courseIDs) {
            Course removeCourse = courseRepository.findByIdAndDeletedFalse(courseID)
                    .orElseThrow(() -> new ResourceNotFoundException("Course remove " + courseID + "is not found"));
            courses.remove(removeCourse);
        }
        cartRepository.save(cart);
    }

    @Override
    public CartDTO getCart(String username) {
        Cart cart = getCartUser(username);
        List<Course> courses = cart.getCourses();
        List<CourseResponse> courseResponses = courses
                .stream().map(c -> {
                    CourseResponse courseResponse = mapper.map(c, CourseResponse.class);
                    courseResponse.setNumRate(ratingRepository.countRatingByStar(c.getId()));
                    return courseResponse;
                })
                .collect(Collectors.toList());
        Long totalMoney = 0L;
        for(Course course : courses){
            totalMoney += course.getPrice();
        }
        return new CartDTO(courseResponses,totalMoney);
    }

    @Override
    public void removeProduct(Long courseID, String username) {
        Cart cart = getCartUser(username);
        List<Course> courses = cart.getCourses();
        Course removeCourse = courseRepository.findByIdAndDeletedFalse(courseID)
                .orElseThrow(() -> new ResourceNotFoundException("Course remove " + courseID + "is not found"));
        courses.remove(removeCourse);
        cartRepository.save(cart);
    }

    private Cart getCartUser(String username) {
        AppUser appUser = userService.findByUsernameAndEnabledTrueAndDeletedFalse(username);
        if (Objects.isNull(appUser)) {
            throw new ResourceNotFoundException("User is not found");
        }
        Optional<Cart> optionalCart = cartRepository.findCartByAppUserId(appUser.getId());
        Cart cart;
        if (optionalCart.isPresent()) {
            cart = optionalCart.get();
            return cart;
        } else {
            cart = new Cart();
            cart.setCourses(new ArrayList<>());
            cart.setAppUser(appUser);
            return cartRepository.save(cart);
        }
    }
}
