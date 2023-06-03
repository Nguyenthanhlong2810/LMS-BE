package ntlong.controller;

import ntlong.dto.ExperienceDTO;
import ntlong.dto.SkillDTO;
import ntlong.payload.response.AdminSettingFirstLoginResponse;
import ntlong.repository.AdminSettingFirstLoginRepository;
import ntlong.response.CourseCategoryDTO;
import ntlong.service.ExperienceService;
import ntlong.service.SkillService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import ntlong.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@Api(tags = "admin")
@RequiredArgsConstructor
public class CategoryController {

    private final UserService userService;

    private final ModelMapper mapper;

    private final AdminSettingFirstLoginRepository firstLoginRepository;

    @GetMapping(value = "/getCourseCategory")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "${CategoryController.getTrainingType}", authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
//            @ApiResponse(code = 404, message = "The user doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> getCourseCategory() {
        CourseCategoryDTO courseCategoryDTO = new CourseCategoryDTO();
        List<ExperienceDTO> experiences = firstLoginRepository.getAdminExperiences()
                .stream().map(e -> mapper.map(e,ExperienceDTO.class)).collect(Collectors.toList());
        courseCategoryDTO.setExperiences(experiences);
        List<SkillDTO> skills = firstLoginRepository.getAdminSkills()
                .stream().map(s -> mapper.map(s,SkillDTO.class)).collect(Collectors.toList());
        courseCategoryDTO.setSkills(skills);
        return new ResponseEntity<>(courseCategoryDTO, HttpStatus.OK);
    }
}
