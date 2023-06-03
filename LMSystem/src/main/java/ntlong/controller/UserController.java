package ntlong.controller;

import com.amazonaws.services.opsworks.model.App;
import ntlong.annotation.CurrentUser;
import ntlong.dto.*;
import ntlong.exception.UploadFailException;
import ntlong.exception.UserNotFoundException;
import ntlong.model.AppUser;
import ntlong.model.AppUserRole;
import ntlong.model.PaginationResponseModel;
import ntlong.payload.request.LoginRequest;
import ntlong.payload.request.RegisterRequest;
import ntlong.repository.UserRepository;
import ntlong.response.BaseResponse;
import ntlong.response.UserResponseDTO;
import ntlong.security.JwtTokenProvider;
import ntlong.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import ntlong.utils.GoogleUtils;
import org.apache.http.client.ClientProtocolException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/users")
@Api(tags = "users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final GoogleUtils googleUtils;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserRepository userRepository;

    @PostMapping("/signin")
    @ApiOperation(value = "${UserController.signin}")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 422, message = "Invalid username/password supplied")})
    public TokenDTO login(@RequestBody LoginRequest user) {
        return userService.signin(modelMapper.map(user, AppUser.class));
    }
    @RequestMapping("/login-google")
    public String loginGoogle(HttpServletRequest request) throws ClientProtocolException, IOException {
        String code = request.getParameter("code");

        if (code == null || code.isEmpty()) {
            return "redirect:/login?google=error";
        }
        String accessToken = googleUtils.getToken(code);

        GooglePojo googlePojo = googleUtils.getUserInfo(accessToken);
        UserDetails userDetail = googleUtils.buildUser(googlePojo);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, null,
                userDetail.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/user";
    }

    @RequestMapping("/client-login-google")
    @Transactional
    public TokenDTO clientLoginGoogle(@RequestBody ClientGoogleRequest googleRequest,
                                    HttpServletRequest request) throws ClientProtocolException, IOException {
        String accessToken = googleRequest.getAccessToken();

        GooglePojo  googlePojo = googleUtils.getUserInfo(accessToken);
        UserDetails userDetail = googleUtils.buildUser(googlePojo);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, null,
                userDetail.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (!userRepository.existsByEmailAndDeletedFalseAndEnabledTrue(googlePojo.getEmail())){
            AppUser appUser = new AppUser();
            appUser.setEmail(googlePojo.getEmail());
            appUser.setUsername(googlePojo.getEmail());
            appUser.setPassword("");
            appUser.setAvatarUrl(googlePojo.getPicture());
            appUser.setEnabled(true);
            userRepository.save(appUser);
        }
        AppUser appUser = userRepository.findByUsernameAndEnabledTrueAndDeletedFalse(googlePojo.getEmail());
        TokenDTO tokenDTO = new TokenDTO("");
        List<AppUserRole> appUserRoles = new ArrayList<>();
        appUserRoles.add(AppUserRole.ROLE_CLIENT);
        tokenDTO.setToken(jwtTokenProvider.createToken(googlePojo.getEmail(),appUserRoles ));
        tokenDTO.setHttpCode(HttpStatus.OK.value());
        return tokenDTO;
    }
    @PutMapping("/update")
    @ApiOperation(value = "Update user")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 422, message = "Invalid username/password supplied")})
    public AppUser updateUser(@RequestPart(value = "file", required = false) MultipartFile file,
                              @RequestPart String appUser)
                              throws UploadFailException, IOException {
        return userService.updateUser(appUser, file);
    }


    @PostMapping("/signup")
    public ResponseEntity<BaseResponse> signUp(@Valid @RequestBody RegisterRequest registerRequest){
        userService.signUp(registerRequest);
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.value(),
                "Đăng kí thành công, hệ thống đã gửi email kích hoạt tài khoản của bạn",null), HttpStatus.OK);
    }

    @GetMapping("/account-verification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token){
        userService.verifyAccount(token);
        return new ResponseEntity<>("Activated account successfully", HttpStatus.OK);
    }

    @GetMapping(value = "/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "${UserController.search}", response = UserResponseDTO.class, authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 404, message = "The user doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public UserResponseDTO search(@ApiParam("Username") @PathVariable String username, @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        return modelMapper.map(userService.search(username), UserResponseDTO.class);
    }

    @GetMapping(value = "/adminInfo")
    @PreAuthorize("hasRole('ROLE_ADMIN')  or hasRole('ROLE_CLIENT')")
    @ApiOperation(value = "${UserController.search}", response = UserResponseDTO.class, authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 404, message = "The user doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public UserResponseDTO getAdminInfo() {
        return modelMapper.map(userService.search("admin"), UserResponseDTO.class);
    }

    @GetMapping(value = "/me")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @ApiOperation(value = "${UserController.me}", response = UserResponseDTO.class, authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public UserResponseDTO whoami(HttpServletRequest req) {
        return modelMapper.map(userService.whoami(req), UserResponseDTO.class);
    }

    @GetMapping("/refresh")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public TokenDTO refresh(HttpServletRequest req) {
        return new TokenDTO(userService.refresh(req.getRemoteUser()));
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity getUserById(@PathVariable("id") Long id) throws UserNotFoundException {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity getUserProfileByCurrenUser(@CurrentUser UserDetails userDetails) throws UserNotFoundException {
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(),"Get User Profile Success",userService.getUserProfileByCurrenUser(userDetails.getUsername())));
    }

    @PostMapping(value = "/set-user-first-login")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
//            @ApiResponse(code = 404, message = "The user doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<BaseResponse> setUserFirstLogin(@RequestBody UserFirstLoginDTO userFirstLoginDTO,
                                                          @CurrentUser UserDetails userDetails) {
        String username = userDetails.getUsername();
        return userService.setUserFirstLogin(userFirstLoginDTO, username);
    }

    @GetMapping("/get-list")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity getListUser(@RequestParam(value = "offset",defaultValue = "1") int offset,
                                      @RequestParam(value = "limit",defaultValue = "25") int limit,
                                      @RequestParam(value = "name", required = false) String name) throws UserNotFoundException {
        Page<AppUser> userList = userService.getList(offset,limit,name);
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.toString(),
                new PaginationResponseModel<>(userList.getContent(), userList.getTotalElements(), offset, limit)), HttpStatus.OK);
    }

}
