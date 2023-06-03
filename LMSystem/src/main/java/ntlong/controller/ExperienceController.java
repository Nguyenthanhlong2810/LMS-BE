package ntlong.controller;

import ntlong.dto.ExperienceDTO;
import ntlong.model.Experience;
import ntlong.payload.response.ExperienceSearchResponse;
import ntlong.service.ExperienceService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/experiences")
@Api(tags = "experiences")
@RequiredArgsConstructor
public class ExperienceController {
    private final ExperienceService experienceService;

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @ApiOperation(value = "${ExperienceController.allExperience}", authorizations = {@Authorization(value = "apiKey")})
    @GetMapping(value = "/all")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
//            @ApiResponse(code = 404, message = "The skill name doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<List<ExperienceDTO>> allExperience(@RequestParam(value = "name", required = false) String name,
                                                             @RequestParam(value = "offset") int offset,
                                                             @RequestParam(value = "limit") int limit) {
        return new ResponseEntity<>(experienceService.findAllSearch(name, offset, limit), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @GetMapping
    public ResponseEntity<List<Experience>> getAll(){
        return ResponseEntity.ok(experienceService.findAllExp());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @GetMapping("/filter-all")
    public ResponseEntity<List<ExperienceSearchResponse>> getAllExpFilter(@RequestParam(value = "offset", required = false, defaultValue = "1") int offset,
                                                                          @RequestParam(value = "limit", required = false, defaultValue = "100") int limit){
        return ResponseEntity.ok(experienceService.findAllSearch(offset,limit));
    }
}
