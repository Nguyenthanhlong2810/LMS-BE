package ntlong.service.impl;

import ntlong.dto.CategoryTrainingDTO;
import ntlong.exception.CustomException;
import ntlong.exception.ResourceNotFoundException;
import ntlong.model.CategoryTraining;
import ntlong.payload.response.CategoryTrainingCourseResponse;
import ntlong.payload.response.CategoryTrainingResponseDTO;
import ntlong.payload.response.categorytraining.CategoryTrainingSearchResponse;
import ntlong.payload.response.categorytraining.MenuInternalResponse;
import ntlong.repository.CategoryTrainingRepository;
import ntlong.repository.CourseRepository;
import ntlong.service.CategoryTrainingService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryTrainingServiceImpl implements CategoryTrainingService {
    private final CategoryTrainingRepository categoryTrainingRepository;

    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;

    @Override
    public void saveCategoryTraining(CategoryTrainingDTO categoryTrainingDTO) {
        CategoryTraining ct = modelMapper.map(categoryTrainingDTO, CategoryTraining.class);
        ct.setDeleted(false);
        validateNOCategoryParent(categoryTrainingDTO, null);

        CategoryTraining category = categoryTrainingRepository.findByName(ct.getName());
        if(Objects.nonNull(category)){
            if(!category.deleted) {
                throw new CustomException("Hạng mục đào tạo "+ ct.getName() + " đã tồn tại!", HttpStatus.BAD_REQUEST);
            }
            category.setDeleted(false);
            categoryTrainingRepository.save(category);
        }else{
            ct.setDeleted(false);
            categoryTrainingRepository.save(ct);
        }
    }

    @Override
    public void updateCategoryTraining(CategoryTrainingDTO categoryTrainingDTO) throws CustomException {
        Optional<CategoryTraining> categoryTrainingOptional = categoryTrainingRepository.findById(categoryTrainingDTO.getId());
        if(categoryTrainingOptional.isEmpty()){
            throw new ResourceNotFoundException("Không tồn tại hạng mục đào tạo");
        }
        CategoryTraining categoryTraining = categoryTrainingOptional.get();
        validateNOCategoryParent(categoryTrainingDTO, categoryTraining);
        checkUpdateCategoryTrainingName(categoryTrainingDTO, categoryTraining);
        //CategoryTraining categoryTraining = modelMapper.map(categoryTrainingDTO,CategoryTraining.class);
        categoryTraining.setDeleted(false);
        categoryTraining.setName(categoryTrainingDTO.getName());
        categoryTraining.setParent(categoryTrainingDTO.getParent());
        categoryTraining.setNo(categoryTrainingDTO.getNo());
        categoryTraining.setDescription(categoryTrainingDTO.getDescription());
        categoryTraining.setTitle(categoryTrainingDTO.getTitle());
        categoryTrainingRepository.save(categoryTraining);
    }

    private void checkUpdateCategoryTrainingName(CategoryTrainingDTO newCategoryTraining,
                                                 CategoryTraining oldCategoryTraining){
        if(!newCategoryTraining.getName().equals(oldCategoryTraining.getName())){
//            courseRepository.updateCategoryTrainingInCourse(newCategoryTraining.getName(),
//                                                            oldCategoryTraining.getName(),
//                                                            newCategoryTraining);
        }
    }

    private void validateNOCategoryParent(CategoryTrainingDTO categoryTrainingDTO, CategoryTraining oldTraining) {
        if (categoryTrainingDTO.getParent() != 0) {
            Optional<CategoryTraining> optional = categoryTrainingRepository.findNoByParent(categoryTrainingDTO.getNo(), categoryTrainingDTO.getParent());
            if (optional.isPresent() && (oldTraining == null || categoryTrainingDTO.getNo() != oldTraining.getNo())) {
                throw new CustomException("Số thự tự: " + categoryTrainingDTO.getNo() + " đã tồn tại", HttpStatus.BAD_REQUEST);
            }
        }
    }

    @Transactional
    @Override
    public void deleteCategoryTraining(Long []ids) throws CustomException {
        for(long id: ids) {
            if (!categoryTrainingRepository.existsById(id)) {
                throw new ResourceNotFoundException("Không tồn tại hạng mục đào tạo") ;
            }

            CategoryTraining categoryTraining = categoryTrainingRepository.getById(id);

            if(courseRepository.existCategoryNameInCourse(categoryTraining.getName())){
                throw new CustomException("Bạn không thể xóa hạng mục đào tạo đang chứa khóa học", HttpStatus.BAD_REQUEST);
            }
            categoryTrainingRepository.updateIsDeleted(id);

            //get sub menu
            List<MenuInternalResponse> menuInternalParentZeros = categoryTrainingRepository.getMenuByParentId(id);
            for(MenuInternalResponse menuloop : menuInternalParentZeros){
                setValueChilds(menuloop);
            }
            //delete
            for(MenuInternalResponse submenu : menuInternalParentZeros){
                deleteCategoryTraining(submenu);
            }
        }
    }

    private void deleteCategoryTraining(MenuInternalResponse menuInternalResponse){
        if(courseRepository.existCategoryNameInCourse(menuInternalResponse.getName())){
            throw new CustomException("Bạn không thể xóa hạng mục đào tạo đang chứa khóa học", HttpStatus.BAD_REQUEST);
        }
        categoryTrainingRepository.updateIsDeleted(menuInternalResponse.getId());
        if(Objects.isNull(menuInternalResponse.getChildsMenu())) {
            return;
        }
        for(MenuInternalResponse child : menuInternalResponse.getChildsMenu()){
            deleteCategoryTraining(child);
        }
    }

    @Override
    public Page<CategoryTraining> getListCategoryTrainingFilter(Integer pageNo, Integer pageSize, String name) {
        Pageable paging = PageRequest.of(pageNo -1,pageSize);
        return categoryTrainingRepository.getListCategoryTrainingFilter(paging, name);
    }

    @Override
    public List<CategoryTraining> getByParentName(String parent) {
        return categoryTrainingRepository.findCategoryTrainingByName(parent);
    }

    @Override
    public String findCategoryByParent(long parent) {
        return categoryTrainingRepository.findByParent(parent);
    }

    @Override
    public List<CategoryTrainingDTO> getListCategoryTrainingFilter() {
        List<CategoryTraining> categoryTrainings = categoryTrainingRepository.getAllListCategoryTraining();
        List<CategoryTrainingDTO> categoryTrainingDTOS = new ArrayList<>();
        for(CategoryTraining categoryTraining : categoryTrainings){
            CategoryTrainingDTO categoryTrainingDTO = new CategoryTrainingDTO();
            categoryTrainingDTO.setId(categoryTraining.getId());
            categoryTrainingDTO.setName(categoryTraining.getName());
            categoryTrainingDTOS.add(categoryTrainingDTO);
        }
        return categoryTrainingDTOS;
    }

    @Override
    public List<MenuInternalResponse> getMenuInternalProgramList() {
        List<MenuInternalResponse> menuInternalParentZeros = categoryTrainingRepository.getMenuByParentId(0l);
        for(MenuInternalResponse menuloop : menuInternalParentZeros){
            setValueChilds(menuloop);
        }
//        List<MenuInternalResponse> menuInternalParentZeros = categoryTrainingRepository.getMenuByParentId(0l);
//        for(MenuInternalResponse menuloop : menuInternalParentZeros){
//            List<MenuInternalResponse> childMenus =categoryTrainingRepository.getMenuByParentId(menuloop.getId());
//            menuloop.setChildsMenu(childMenus.stream().sorted(Comparator.comparing(MenuInternalResponse::getNumOrder)).collect(Collectors.toList()));
//            for (MenuInternalResponse childMenusLoop : childMenus){
//                List<MenuInternalResponse> childMenu2s =categoryTrainingRepository.getMenuByParentId(childMenusLoop.getId());
//                childMenusLoop.setChildsMenu(childMenu2s.stream().sorted(Comparator.comparing(MenuInternalResponse::getNumOrder)).collect(Collectors.toList()));
//            }
//        }
        return menuInternalParentZeros;
    }

    @Override
    public List<CategoryTrainingSearchResponse> getCategoryTrainingSearch() {
        List<CategoryTraining> categoryTrainings = categoryTrainingRepository.getAllListCategoryTraining();
        List<CategoryTrainingSearchResponse> categoryTrainingSearchResponses = new ArrayList<>();
        for(CategoryTraining categoryTraining : categoryTrainings){
            CategoryTrainingSearchResponse categoryTrainingSearchResponse = CategoryTrainingSearchResponse
                    .builder()
                    .id(categoryTraining.getId())
                    .name(categoryTraining.getName())
                    .build();
            int totalCourse = courseRepository.getTotalCourseHasCategoryTraining(categoryTraining.getName());
            if(totalCourse > 0){
                categoryTrainingSearchResponse.setTotalCourse(totalCourse);
                categoryTrainingSearchResponses.add(categoryTrainingSearchResponse);
            }
        }
        return categoryTrainingSearchResponses;
    }

    @Override
    public List<CategoryTrainingCourseResponse> getCategoryTrainingWithCourses() {
        List<CategoryTraining> categoryTrainings = categoryTrainingRepository.getAllListCategoryTraining();
        List<CategoryTrainingCourseResponse> categoryTrainingCourseResponses = new ArrayList<>();
        for(CategoryTraining categoryTraining : categoryTrainings){
            CategoryTrainingCourseResponse categoryTrainingResponse = new CategoryTrainingCourseResponse();
            categoryTrainingResponse.setName(categoryTraining.getName());
            categoryTrainingResponse.setId(categoryTraining.getId());
            categoryTrainingResponse.setTitle(categoryTraining.getTitle());
            categoryTrainingResponse.setDescription(categoryTraining.getDescription());
            categoryTrainingResponse.setData(courseRepository.getCourseByCategoryTraining(categoryTraining.getId()));
            categoryTrainingCourseResponses.add(categoryTrainingResponse);
        }
        Comparator<CategoryTrainingCourseResponse> sortComparator = Comparator.comparingInt(o -> o.getData().size());
        categoryTrainingCourseResponses.sort(sortComparator);
        return categoryTrainingCourseResponses;
    }

    private void setValueChilds(MenuInternalResponse menu){
        List<MenuInternalResponse> subMenus = categoryTrainingRepository.getMenuByParentId(menu.getId());
        if(!CollectionUtils.isEmpty(subMenus)){
            menu.setChildsMenu(subMenus.stream().sorted(Comparator.comparing(MenuInternalResponse::getNumOrder)).collect(Collectors.toList()));
            for (MenuInternalResponse menuInternalResponse:subMenus){
                setValueChilds(menuInternalResponse);
            }
        }
    }
}
