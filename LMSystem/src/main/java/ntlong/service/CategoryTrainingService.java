package ntlong.service;

import ntlong.dto.CategoryTrainingDTO;
import ntlong.exception.CustomException;
import ntlong.model.CategoryTraining;
import ntlong.payload.response.CategoryTrainingCourseResponse;
import ntlong.payload.response.categorytraining.CategoryTrainingSearchResponse;
import ntlong.payload.response.categorytraining.MenuInternalResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryTrainingService {
    void saveCategoryTraining(CategoryTrainingDTO categoryTrainingDTO);

    void updateCategoryTraining(CategoryTrainingDTO categoryTrainingDTO) throws CustomException;

    void deleteCategoryTraining(Long [] ids) throws CustomException;

    Page<CategoryTraining> getListCategoryTrainingFilter(Integer pageNo, Integer pageSize, String name);

    List <CategoryTraining> getByParentName(String parent);

    String findCategoryByParent(long parent);

    List<CategoryTrainingDTO> getListCategoryTrainingFilter();

    List<MenuInternalResponse> getMenuInternalProgramList();

    List<CategoryTrainingSearchResponse> getCategoryTrainingSearch();

    List<CategoryTrainingCourseResponse> getCategoryTrainingWithCourses();
}
