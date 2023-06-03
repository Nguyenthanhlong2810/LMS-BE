package ntlong.controller;

import ntlong.dto.CategoryTrainingDTO;
import ntlong.payload.response.CategoryTrainingCourseResponse;
import ntlong.payload.response.CategoryTrainingResponseDTO;
import ntlong.model.CategoryTraining;
import ntlong.model.PaginationResponseModel;
import ntlong.payload.response.categorytraining.CategoryTrainingSearchResponse;
import ntlong.payload.response.categorytraining.MenuInternalResponse;
import ntlong.response.BaseResponse;
import ntlong.response.ResponseMessage;
import ntlong.service.CategoryTrainingService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/category")
@AllArgsConstructor
public class CategoryTrainingController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    private final CategoryTrainingService categoryTrainingService;

    @PostMapping("/create-category-training")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> createCategoryTraining(@RequestBody @Valid CategoryTrainingDTO request) {
        log.debug("create program: {}", request);
        categoryTrainingService.saveCategoryTraining(request);
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.value(), "Tạo thành công"), HttpStatus.OK);
    }

    @PutMapping("/update-category-training")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> updateCategoryTraining(@RequestBody CategoryTrainingDTO request) {

        log.debug("update category training: {}", request.getId());
        categoryTrainingService.updateCategoryTraining(request);
        return new ResponseEntity<>(new BaseResponse("Update category training success"), HttpStatus.OK);
    }

    @DeleteMapping("/delete-category-training")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> deleteCategoryTraining(@RequestBody Long[] ids) {
        log.debug("delete program: {}", ids);
        categoryTrainingService.deleteCategoryTraining(ids);
        return new ResponseEntity<>(new BaseResponse("Delete category training success"), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity getListCategoryTraining(@RequestParam(defaultValue = "0", required = false) Integer pageNo,
                                                  @RequestParam(defaultValue = "10", required = false) Integer pageSize,
                                                  @RequestParam(defaultValue = "", value = "name",
                                                          required = false) String name) {
        log.debug("get list category training");
        Page<CategoryTraining> categoryTrainings =
                categoryTrainingService.getListCategoryTrainingFilter(pageNo, pageSize, name);
        List<CategoryTraining> list = categoryTrainings.getContent();
        List<CategoryTrainingResponseDTO> responseDTOList = new ArrayList<>();
        for (CategoryTraining ct : list) {
            CategoryTrainingResponseDTO response = new CategoryTrainingResponseDTO();
            if (ct.getParent() != 0) {
                String parentName = categoryTrainingService.findCategoryByParent(ct.getParent());
                response.setParentName(parentName);
            }
            response.setId(ct.getId());
            response.setName(ct.getName());
            response.setParent(ct.getParent());
            response.setNo(ct.getNo());
            response.setCreatedBy(ct.getCreatedBy());
            response.setCreatedDate(ct.getCreatedDate());
            response.setDescription(ct.getDescription());
            response.setTitle(ct.getTitle());
            responseDTOList.add(response);
        }
        PaginationResponseModel<CategoryTrainingResponseDTO> res = new PaginationResponseModel<>();
        if (categoryTrainings.hasContent()) {
            long totalRecords = categoryTrainings.getTotalElements();
            res = new PaginationResponseModel<>(responseDTOList, totalRecords, pageNo, pageSize);
        }
        return new ResponseEntity<>(new BaseResponse("get list success", res), HttpStatus.OK);
    }

    @GetMapping("/get-courses")
    public ResponseEntity<BaseResponse> getCategoryTrainingWithCourses(){
        List<CategoryTrainingCourseResponse> categoryTrainingCourseResponses = categoryTrainingService
                .getCategoryTrainingWithCourses();
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.value(),
                "Get category training with course successfully",categoryTrainingCourseResponses),
                HttpStatus.OK);
    }

    @GetMapping("/auto-complete")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity getListForAutoComplete(@RequestParam(value = "parentName", required = false) String parentName) {
        List<CategoryTraining> category = categoryTrainingService.getByParentName(parentName);
        return new ResponseEntity<>(new BaseResponse("get list success", category), HttpStatus.OK);

    }

    @GetMapping("/filter-all")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity getListCategoryTrainingFilter() {
        List<CategoryTrainingSearchResponse> categoryTrainings = categoryTrainingService.getCategoryTrainingSearch();
        return new ResponseEntity<>(new BaseResponse("get list success", categoryTrainings), HttpStatus.OK);
    }

    /***
     * Create level menu internal program from category training
     * @return
     */
    @GetMapping("/menu-internal")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity getMenuInterProgram() {
        List<MenuInternalResponse> menus = categoryTrainingService.getMenuInternalProgramList();
        return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), "Success", menus, (long) menus.size()));
    }

    /**
     * Lay tat ca hang muc dao tao
     *
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity getCategoryTrainingAll(@RequestParam(value = "language", required = false) String language) {
        List<CategoryTrainingDTO> categoryTrainings = categoryTrainingService.getListCategoryTrainingFilter();
        return new ResponseEntity<>(new BaseResponse("get list success", categoryTrainings), HttpStatus.OK);
    }
}
