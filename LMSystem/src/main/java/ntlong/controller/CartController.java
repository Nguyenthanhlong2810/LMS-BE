package ntlong.controller;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import ntlong.annotation.CurrentUser;
import ntlong.dto.CartDTO;
import ntlong.model.Cart;
import ntlong.response.BaseResponse;
import ntlong.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@AllArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping(value = "/add-product")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<BaseResponse> addProduct(@RequestParam("courseID") Long courseID,
                                                   @CurrentUser UserDetails user) {
        cartService.addProduct(courseID,user.getUsername());
        return new ResponseEntity<>(new BaseResponse("Thêm sản phẩm vào giỏ hàng thành công", HttpStatus.OK),HttpStatus.OK);
    }

    @PutMapping(value = "/remove-products")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<BaseResponse> removeProducts(@RequestBody List<Long> courseIDs,
                                                      @CurrentUser UserDetails user) {
        cartService.removeProducts(courseIDs,user.getUsername());
        return new ResponseEntity<>(new BaseResponse("Xóa sản phẩm khỏi giỏ hàng thành công", HttpStatus.OK),HttpStatus.OK);
    }

    @DeleteMapping(value = "/remove-products/{courseID}")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<BaseResponse> removeProduct(@PathVariable Long courseID,
                                                      @CurrentUser UserDetails user) {
        cartService.removeProduct(courseID,user.getUsername());
        CartDTO cart = cartService.getCart(user.getUsername());
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.value(), "Xóa sản phẩm khỏi giỏ hàng thành công",cart),HttpStatus.OK);
    }

    @GetMapping(value = "/get-cart")
    @ApiResponses(value = {//';///
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<BaseResponse> getCart(@CurrentUser UserDetails user) {
        CartDTO cart = cartService.getCart(user.getUsername());
        return new ResponseEntity<>(
                new BaseResponse(HttpStatus.OK.value(), "Lấy thông tin giỏ hàng thành công",cart),HttpStatus.OK);
    }


}
