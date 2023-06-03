package ntlong.controller;

import ntlong.dto.SkillDTO;
import ntlong.model.Skill;
import ntlong.payload.response.SkillSearchResponse;
import ntlong.service.SkillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/skills")
@Api(tags = "skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    //to pageable
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "${SkillNameController.skill-name}", authorizations = {@Authorization(value = "apiKey")})
    @GetMapping(value = "/page")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 404, message = "The skill name doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<Page<Skill>> getAllSkill(@RequestParam(value = "page") Integer page,
                                                   @RequestParam(value = "size") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(skillService.getAllSkill(pageable), HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @ApiOperation(value = "${SkillNameController.skill-name}", authorizations = {@Authorization(value = "apiKey")})
    @GetMapping(value = "/filter-all")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 404, message = "The skill name doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<List<SkillSearchResponse>> allSkillFilter(@RequestParam(value = "offset", required = false, defaultValue = "1") int offset,
                                                                    @RequestParam(value = "limit", required = false, defaultValue = "100" ) int limit) {
        return new ResponseEntity<>(skillService.getAllSkill(offset,limit), HttpStatus.OK);
    }

}

