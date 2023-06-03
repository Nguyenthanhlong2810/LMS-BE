package ntlong.service.impl;

import lombok.RequiredArgsConstructor;
import ntlong.converter.LessonStructureConverter;
import ntlong.converter.LessonStructureInformationConverter;
import ntlong.dto.*;
import ntlong.dto.delete.ListIdDTO;
import ntlong.enums.StatusCourseEnum;
import ntlong.enums.TypeAssignEnum;
import ntlong.exception.CustomException;
import ntlong.exception.ResourceNotFoundException;
import ntlong.exception.UserNotFoundException;
import ntlong.model.*;
import ntlong.payload.request.CourseSettingRequest;
import ntlong.payload.response.course.ProcessUserCourseResponse;
import ntlong.payload.response.course.ProgressUserCourseResponse;
import ntlong.repository.*;
import ntlong.service.AmazonClient;
import ntlong.service.CourseService;
import ntlong.service.UserService;
import ntlong.utils.CommonImpl;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final CategoryTrainingRepository categoryTrainingRepository;

    private final AmazonClient amazonClient;
    private final TagRepository tagRepository;
    private final RequirementCourseRepository requirementCourseRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    private final LessonStructureConverter lessonStructureConverter;

    private final LessonStructureInformationConverter lessonStructureInformationConverter;

    private final LessonContentUploadRepository lessonContentUploadRepository;

    private final ExperienceRepository experienceRepository;

    private final UserService userService;
    private final LessonStructureRepository lessonStructureRepository;
    private final ModelMapper modelMapper;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final CourseSettingRepository courseSettingRepository;

    private final CommonImpl common;

    private final LessonContentUploadHistoryRepository lessonContentUploadHistoryRepository;

    

    @PersistenceContext
    private EntityManager entityManager;

    private static final String INDEFINITE = "INDEFINITE";

    private final AssignCourseRepository assignCourseRepository;

    private String getUrl(UploadFileDTO uploadFileDTO, String fileName) {
        if(Objects.isNull(uploadFileDTO)){
            return null;
        }
        for (FileDTO fileDTO : uploadFileDTO.getFiles()) {
            if (fileName.equals(fileDTO.getFileName())) {
                return fileDTO.getPreviewUrl();
            }
        }
        return null;
    }

    @Override
    public CourseDTO saveOrUpdate(CourseDTO courseDTO, MultipartFile[] files, boolean isUpdate) throws IOException {
        validateCourse(courseDTO,isUpdate);
        Course course;
        if(isUpdate){
            course = courseRepository.findByLongId(courseDTO.getId());
        }else{
            course = new Course();
        }
        course.setName(courseDTO.getName());

        CategoryTraining categoryTraining = categoryTrainingRepository.findByName(courseDTO.getCategoryTrainingName());
        course.setCategoryTraining(categoryTraining);

        if(Objects.nonNull(courseDTO.getIsHot())) {
            course.setHot(courseDTO.getIsHot());
        }

        UploadFileDTO uploadFileDTO = null;
        if (Objects.nonNull(files)) {
            uploadFileDTO = amazonClient.uploadMultiFile(files);
        }
        if(Objects.isNull(courseDTO.getFileNameOfferBy())){
            course.setFileNameOfferBy(null);
            course.setPathOfferBy(null);
        }else{
            String pathOfferBy = getUrl(uploadFileDTO, courseDTO.getFileNameOfferBy());
            if(Objects.nonNull(pathOfferBy)){
                course.setFileNameOfferBy(courseDTO.getFileNameOfferBy());
                course.setPathOfferBy(pathOfferBy);
            }
        }

        if(Objects.isNull(courseDTO.getFileNameProvideBy())){
            course.setFileNameProvideBy(null);
            course.setPathProvideBy(null);
        }else{
            String pathProvideBy = getUrl(uploadFileDTO, courseDTO.getFileNameProvideBy());
            if(Objects.nonNull(pathProvideBy)){
                course.setFileNameProvideBy(courseDTO.getFileNameProvideBy());
                course.setPathProvideBy(pathProvideBy);
            }
        }

        Set<Tag> tagSet = new HashSet<>();
        for (String tag : courseDTO.getTags()) {
            Tag tmp;
            if (tagRepository.existsByName(tag)) {
                tmp = tagRepository.findByName(tag);
                tagSet.add(tmp);
            } else {
                tmp = new Tag();
                tmp.setName(tag);
                tagRepository.save(tmp);
            }
            tagSet.add(tmp);
        }
        Set<Tag> tagResults = new HashSet<>(tagSet);
        course.setTags(tagResults);


        course.setSummary(courseDTO.getSummary());


        course.setDetail(courseDTO.getDetail());

        if (Objects.nonNull(courseDTO.getFileNamePreview()) && Objects.nonNull(uploadFileDTO)) {
            String pathPreview = getUrl(uploadFileDTO, courseDTO.getFileNamePreview());
            if(Objects.nonNull(pathPreview)){
                course.setFileNamePreview(courseDTO.getFileNamePreview());
                course.setPathPreview(pathPreview);
            }
        } else {
            if(Objects.isNull(courseDTO.getFileNamePreview())) {
                DefaultPreviewImage defaultPreviewImage = common.getPreviewImage();
                if (Objects.isNull(defaultPreviewImage)) {
                    defaultPreviewImage = common.createPreviewImage();
                }
                course.setFileNamePreview(defaultPreviewImage.getImageName());
                course.setPathPreview(defaultPreviewImage.getImageLink());
            }
        }


        Set<RequirementCourse> requirementCourses = new HashSet<>();
        for (String string : courseDTO.getRequirementCourses()) {
            if (requirementCourseRepository.existsByName(string)) {
                requirementCourses.add(requirementCourseRepository.findByName(string));
            } else {
                RequirementCourse requirementCourse = new RequirementCourse();
                requirementCourse.setName(string);
                requirementCourses.add(requirementCourse);
            }
        }
        Set<RequirementCourse> results = new HashSet<>(requirementCourses);
        course.setRequirementCourses(results);

        Set<Skill> skillSet = new HashSet<>();
        for (String skillName : courseDTO.getSkills()) {
            if (skillRepository.existsByNameAndDeletedFalse(skillName)) {
                Skill skill = skillRepository.findByNameAndDeletedFalse(skillName);
                skillSet.add(skill);
            }
        }
        course.setSkills(skillSet);

        Set<Experience> experiences = new HashSet<>();
        for (String expName : courseDTO.getExperiences()) {
            if (experienceRepository.existsByNameAndDeletedFalse(expName)) {
                Experience exp = experienceRepository.findByName(expName);
                experiences.add(exp);
            }
        }
        course.setExperiences(experiences);

        course.setFreeCourse(courseDTO.getFreeCourse());

        if(Objects.nonNull(courseDTO.getPrice())){
            course.setPrice(courseDTO.getPrice());
        }
        if(Objects.nonNull(courseDTO.getInstructorName())) {
            course.setInstructorName(courseDTO.getInstructorName());
        }
        course.setDeleted(false);
        return modelMapper.map(courseRepository.save(course), CourseDTO.class);
    }

    private void validateCourse(CourseDTO courseDTO, boolean isUpdate){
        if(isUpdate && Objects.isNull(courseDTO.getId())){
            throw new CustomException("Course Id is null", HttpStatus.BAD_REQUEST);
        }
        if(isUpdate && !courseRepository.existsById(courseDTO.getId())){
            throw new CustomException("Course with id "+ courseDTO.getId()+ "is not found", HttpStatus.BAD_REQUEST);
        }

        if (Objects.isNull(courseDTO.getName())) {
            throw new CustomException("Course name is null", HttpStatus.BAD_REQUEST);
        }
        if (!isUpdate && courseRepository.existsByNameAndDeletedFalse(courseDTO.getName())) {
            throw new EntityExistsException("Course name is exists");
        }
        if (Objects.isNull(courseDTO.getCategoryTrainingName())) {
            throw new CustomException("Category training name is null", HttpStatus.BAD_REQUEST);
        }
        if (!categoryTrainingRepository.existsByName(courseDTO.getCategoryTrainingName())) {
            throw new ResourceNotFoundException("Category training name is not found");
        }
        if (Objects.isNull(courseDTO.getTags())) {
            throw new CustomException("Tag list is null", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(courseDTO.getSummary())) {
            throw new CustomException("Summary is null", HttpStatus.BAD_REQUEST);
        }

        if (Objects.isNull(courseDTO.getDetail())) {
            throw new CustomException("Detail is null", HttpStatus.BAD_REQUEST);
        }

        if (Objects.isNull(courseDTO.getRequirementCourses())) {
            throw new CustomException("Requirements is null", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(courseDTO.getSkills())) {
            throw new CustomException("Skills is null", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(courseDTO.getExperiences())) {
            throw new CustomException("Experiences is null", HttpStatus.BAD_REQUEST);
        }

        if(!courseDTO.getFreeCourse() && Objects.isNull(courseDTO.getPrice())){
            throw new CustomException("Price is required for free course", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Page<CourseResponse> getListCourse(String name, Boolean isActivated, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset-1, limit);

        Page<Course> courses = courseRepository.searchCourseList
                (name, isActivated, pageable);
        return new PageImpl<>(courses.getContent()
                                            .stream().map(this::mapCourseToCourseResponse)
                                            .collect(Collectors.toList()));
    }

    private CourseResponse mapCourseToCourseResponse(Course c){
        CourseResponse courseResponse = modelMapper.map(c,CourseResponse.class);
        courseResponse.setCategoryTrainingName(c.getCategoryTraining().getName());
        courseResponse.setTags(c.getTags()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toList()));
        courseResponse.setExperiences(c.getExperiences()
                .stream()
                .map(Experience::getName).collect(Collectors.toList()));
        courseResponse.setSkills(c.getSkills()
                .stream()
                .map(Skill::getName).collect(Collectors.toList()));
        courseResponse.setRequirementCourses(c.getRequirementCourses()
                .stream()
                .map(RequirementCourse::getName).collect(Collectors.toList()));
        if(Objects.nonNull(c.getCourseSetting())){
            courseResponse.setActivated(c.getCourseSetting().getIsActivated());
        }else{
            courseResponse.setActivated(false);
        }
        return courseResponse;
    }
    @Override
    public void deleteById(Long id) {
        long countCourse = courseSettingRepository.findAllByCourseId(id)
                .stream()
                .filter(CourseSetting::getIsActivated)
                .count();
        if (countCourse > 0) {
            log.error("Delete failed " + id);
            throw new CustomException("Không thể xóa khóa học đang kích hoạt", HttpStatus.BAD_REQUEST);
        }
        courseRepository.deleteCourse(id);
    }
    @Override
    public CourseResponse findById(long id) {
        Course course = courseRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new ResourceNotFoundException("Id does not existed"));
        return mapCourseToCourseResponse(course);
    }

    @Override
    public void deleteByIds(ListIdDTO ids) {
        for(Long id : ids.getIds()){
            deleteById(id);
        }
    }


    @Override
    public Optional<CourseSetting> getSettingCourse(Long id) {
        return courseSettingRepository.getSettingCourseById(id);
    }

    @Override
    public CourseSetting saveOrUpdateSettingCourse(CourseSettingRequest courseSettingRequest) {
        CourseSetting courseSetting;
        if(Objects.nonNull(courseSettingRequest.getId())){
            courseSetting = courseSettingRepository.findById(courseSettingRequest.getId())
                    .orElseThrow(() -> new EntityNotFoundException("course setting is not found"));
        }else{
            courseSetting = new CourseSetting();
        }

        Course course = courseRepository.findByIdAndDeletedFalse(courseSettingRequest.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Course is not found"));

        courseSetting.setCourse(course);
        courseSetting.setIsCertificated(courseSettingRequest.getIsCertificated());
        courseSetting.setIsActivated(courseSettingRequest.getIsActivated());
        courseSetting.setIsCompleteByOrder(courseSettingRequest.getCompletedByOrder());

        for (LessonContentUploadDTO lessonContentUploadDTO : courseSettingRequest.getLessonContentUploads()) {
            List<LessonContentUpload> lessonContentUploads = lessonContentUploadRepository
                    .findLessonContentUploadsByLessonStructureIdAndContentUploadId
                            (lessonContentUploadDTO.getLessonStructureId(), lessonContentUploadDTO.getContentUploadId());
            for (LessonContentUpload item : lessonContentUploads) {
                item.setCanDownload(lessonContentUploadDTO.isCanDownload());
                item.setCompletedOpen(lessonContentUploadDTO.isCompletedOpen());
                item.setConditionPass(lessonContentUploadDTO.getConditionPass());
            }
            lessonContentUploadRepository.saveAll(lessonContentUploads);
        }

        courseSettingRepository.save(courseSetting);

        return courseSetting;
    }

    @Override
    public CourseStructureDTO getCourseStructureById(Long courseId) {
        Course course = courseRepository.findByIdAndDeletedFalse(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course does not existed"));
        CourseSetting courseSetting = courseSettingRepository.findByCourseId(courseId);
        CourseStructureDTO courseStructureDTO = new CourseStructureDTO();
        courseStructureDTO.setId(course.getId());
        courseStructureDTO.setCourseName(course.getName());

        //course setting
        if(Objects.nonNull(courseSetting)){
            courseStructureDTO.setCompletedByOrder(courseSetting.getIsCompleteByOrder());
        }
        List<LessonStructure> lessonStructures = course.getLessonStructures();
        List<LessonStructureDTO> lessonStructureDTOS = lessonStructures.stream()
                .sorted(Comparator.comparing(LessonStructure::getSortOrder))
                .map(lessonStructureConverter::convertToDTO)
                .collect(Collectors.toList());
        courseStructureDTO.setLessonStructures(lessonStructureDTOS);
        return courseStructureDTO;
    }

    @Override
    public CourseInformationStructureDTO getLessonStructureById(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Khóa học không tồn tại"));
        CourseInformationStructureDTO dto = new CourseInformationStructureDTO();
        dto.setId(course.getId());
        dto.setCourseName(course.getName());
        //dto.setCompletedByOrder(course());
        List<LessonStructure> lessonStructures = course.getLessonStructures();
        List<LessonStructureInformationDTO> lessonStructureInformationDTOS = lessonStructures.stream()
                .sorted(Comparator.comparing(LessonStructure::getSortOrder))
                .map(lessonStructureInformationConverter::convertToDTO)
                .collect(Collectors.toList());
        dto.setLessonStructures(lessonStructureInformationDTOS);
        dto.setTotalDuration(lessonStructureInformationDTOS.stream().mapToLong(LessonStructureInformationDTO::getTotalDuration).sum());
        dto.setLessonCount(lessonStructureInformationDTOS.stream().mapToLong(LessonStructureInformationDTO::getLessonCount).sum());
        dto.setTotalDurationDisplay(lessonStructureInformationConverter.convertSecondToHMSString(dto.getTotalDuration()));
        return dto;
    }

    @Override
    public CourseOverallDataDTO getCourseOverallDataUpdate(Long courseId,String username) {
        CourseOverallDataDTO overallDataDTO;
        CourseSetting courseSetting = courseSettingRepository.findByCourseId(courseId);
        Course course = courseRepository.findByIdAndDeletedFalse(courseId).orElseThrow(() -> new ResourceNotFoundException("Course does not exist"));
        overallDataDTO = mapToOverAllData(course);
        if (Objects.nonNull(courseSetting)) {
            overallDataDTO.setIsCertificated(courseSetting.getIsCertificated());
        }

        if(Objects.nonNull(username)){
            Long userId = userRepository.findIdByUsername(username);
            if(Objects.nonNull(userId)){
                overallDataDTO.setAssigned(assignCourseRepository.checkAssignedCourse(courseId,userId));
            }
        }
        overallDataDTO.setRequirementCourses(course.getRequirementCourses());

        if(Objects.nonNull(course.getPathOfferBy()) && !course.getPathOfferBy().isEmpty()){
            overallDataDTO.setIsOfferedBy(true);
            overallDataDTO.setProvidedPath(course.getPathOfferBy());
        }else {
            if(Objects.nonNull(course.getPathProvideBy()) && !course.getPathProvideBy().isEmpty()){
                overallDataDTO.setProvidedPath(course.getPathProvideBy());
                overallDataDTO.setIsOfferedBy(false);
            }
        }
        List<LessonStructure> lessonStructure = lessonStructureRepository.findByCourseId(courseId);
        List<LessonContentUpload> lessonContentUploads = lessonContentUploadRepository.findLessonContentUploadsByLessonStructureIdIn(lessonStructure.parallelStream().map(LessonStructure::getId).collect(Collectors.toList()));
        if (!lessonContentUploads.isEmpty()) {
            overallDataDTO.setDownloadableCount(lessonContentUploads.stream().filter(LessonContentUpload::isCanDownload).count());
            long duration = 0L;
            for (LessonStructure ls : lessonStructure) {
                List<Long> allLessonDuration = lessonContentUploadRepository.findAllDurationByLessonStructureId(ls.getId());
                if (allLessonDuration.stream().noneMatch(Objects::nonNull)) {
                    duration += 0;
                } else {
                    duration += allLessonDuration.stream().reduce(0L, Long::sum);
                }
            }
            overallDataDTO.setTotalDuration(duration);
        }
        return overallDataDTO;
    }

    private CourseOverallDataDTO mapToOverAllData(Course course) {
        CourseOverallDataDTO overallDataDTO = new CourseOverallDataDTO();
        overallDataDTO.setRequirementCourses(course.getRequirementCourses());
        overallDataDTO.setSummary(course.getSummary());
        overallDataDTO.setCourseDescription(course.getDetail());
        overallDataDTO.setCourseName(course.getName());
        overallDataDTO.setTagList(course.getTags());
        overallDataDTO.setVideoUrl(course.getPathPreview());
        overallDataDTO.setInstructorName(course.getInstructorName());
        overallDataDTO.setFree(course.getFreeCourse());

//        overallDataDTO.setApproveName(course.getApproveName());
//        overallDataDTO.setRequireApproval(course.isRequireApproval());
        return overallDataDTO;
    }



    @Override
    public Page<CourseDTO> getSuggestCoursesByUser(Integer pageNo, Integer pageSize, Long userId) throws UserNotFoundException {
        pageNo = pageNo <= 0 ? 0 : pageNo - 1;
        Pageable paging = PageRequest.of(pageNo, pageSize);
        List<Long> courseAssigned = assignCourseRepository.getCourseIdByUserId(userId);
        List<Course> courses = courseRepository.findAll().stream().filter(c ->
            Objects.nonNull(c.getCourseSetting()) && c.getCourseSetting().getIsActivated()
                    && !c.deleted && !courseAssigned.contains(c.getId())).collect(Collectors.toList());

        UserInforDTO userDTO = userService.getUserById(userId);
        Set<String> experiencesName = userDTO.getExperiences().stream().map(Experience::getName).collect(Collectors.toSet());
        Set<String> skillsName = userDTO.getSkillsInteresting().stream().map(Skill::getName).collect(Collectors.toSet());

        List<Course> courseSkill = logicRecommend(skillsName, courses);
        List<Course> courseExperiences = logicRecommend(experiencesName,courses);
        List<Course> suggestCourse = new ArrayList<>();

        addCourseToRecommend(suggestCourse, courseSkill);
        addCourseToRecommend(suggestCourse, courseExperiences);

        List<CourseDTO> courseDTOS = suggestCourse.stream()
                .map(course -> {
                    CourseDTO courseDTO = modelMapper.map(course, CourseDTO.class);
                    courseDTO.setCategoryTrainingName(course.getCategoryTraining().getName());
                    return courseDTO;
                })
                .collect(Collectors.toList());
        int totalPages = courseDTOS.size() / pageSize;

        int max = pageNo >= totalPages ? courseDTOS.size() : pageSize * (pageNo + 1);
        int min = pageNo > totalPages ? max : pageSize * pageNo;
        List<CourseDTO> returnCourses = courseDTOS.subList(min, max);
        returnCourses.forEach(courseDTO -> {
            //courseDTO.setAssigned(assignCourseRepository.existsAssignCourseByAppUser_IdAndCourse_Id(userId, courseDTO.getId()));
            courseDTO.setPathPreview(courseDTO.getPathPreview());
        });
        return new PageImpl<>(returnCourses, paging, courseDTOS.size());
    }

    private List<Course> addCourseToRecommend(List<Course> suggestCourse, List<Course> courseToAdd) {
        for (int i = courseToAdd.size() - 1; i >= 0; i--) {
            if (!suggestCourse.contains(courseToAdd.get(i))) {
                suggestCourse.add(courseToAdd.get(i));
            }
        }
        return suggestCourse;
    }

    private List<Course> logicRecommend(Set<String> recommendSet, List<Course> courses) {
        List<Course> suggestCourses = new ArrayList<>();
        Map<Long, Integer> courseMap = new HashMap<>();
        for (Course course : courses) {
            Set<String> skillsName = course.getSkills().stream().map(Skill::getName).collect(Collectors.toSet());
            for (String tag : skillsName) {
                if (recommendSet.stream().anyMatch(rs -> rs.contains(tag))) {
                    if (courseMap.containsKey(course.getId())) {
                        int n = courseMap.get(course.getId());
                        n++;
                        courseMap.put(course.getId(), n);
                    } else {
                        courseMap.put(course.getId(), 1);
                    }
                }
            }
        }
        List<Map.Entry<Long, Integer>> courseList = new ArrayList<>(courseMap.entrySet());
        courseList.sort(Map.Entry.comparingByValue());
        courseList.forEach(course -> {
            Optional<Course> foundedCourse = courseRepository.findByIdAndDeletedFalse(course.getKey());
            foundedCourse.ifPresent(suggestCourses::add);
        });
        return suggestCourses;
    }

    @Override
    public Page<CourseHistoryDTO> getListCourseHistoryByUserId(Long userId, String courseName,
                                                               StatusCourseEnum progressStatus, TypeAssignEnum courseType,
                                                               Date beforeDate, Date afterDate,
                                                               Integer pageNo, Integer pageSize)
            throws UserNotFoundException {
        Pageable paging = PageRequest.of(pageNo - 1, pageSize);
        if (userService.getUserById(userId) == null) throw new UserNotFoundException("user does not existed");
        Page<CourseHistoryDTO> courseHistoryDTOS = courseHistoryFilter(paging,userId,courseName,progressStatus, courseType, beforeDate, afterDate);
        for (CourseHistoryDTO tempCourse : courseHistoryDTOS) {
            tempCourse.setPathPreviewIMG(tempCourse.getPathPreviewIMG());

            int totalContentUpload = courseRepository.getTotalContentUpload(tempCourse.getCourseId());

            int totalCompletedContentUpload = lessonContentUploadHistoryRepository.getTotalCompletedContentUpload(
                    tempCourse.getCourseId(),userId);

            if(tempCourse.getStatusCourse().equals(StatusCourseEnum.COMPLETED)){
                tempCourse.setCourseProgress(100+"%");
            }else {
                tempCourse.setCourseProgress(calculateLessonProgress(totalContentUpload, totalCompletedContentUpload));
            }
            if (tempCourse.getDueDate() != null) {
                int remainingTime = (int) Duration.between(LocalDateTime.now(), Instant.ofEpochMilli(tempCourse.getDueDate().getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()).toDays();
                tempCourse.setRemainingTime(Math.max(remainingTime, 0));
            }
        }

        return courseHistoryDTOS;
    }


    public Page<CourseHistoryDTO> courseHistoryFilter(Pageable paging, Long userId, String courseName,
                                                      StatusCourseEnum progressStatus, TypeAssignEnum courseType,
                                                      Date beforeDate, Date afterDate) {
        StringBuilder querySql = new StringBuilder("select new ntlong.dto.CourseHistoryDTO(c.id, c.name, c.pathPreview, " +
                "c.categoryTraining.name," +
                "c.summary, c.detail,ac.toTime, ac.fromTime," +
                " c.instructorName, c.lastUpdated, ac.completedDate,ac.progressStatus, ac.typeAssign) " +
                "from AssignCourse ac inner join ac.course c inner join c.courseSetting cs where ac.deleted = false " +
                "and ac.appUser.id = :userId and " +
                "(unaccent(lower(c.name)) like unaccent(lower(concat('%', trim(:courseName) ,'%'))) or :courseName is null or :courseName='') " +
                "and (:assignType IS NULL or ac.typeAssign = :assignType or :assignType='')" +
                " and (:progressStatus is null or ac.progressStatus = :progressStatus or :progressStatus = '') " +
                "and cs.isActivated = true and c.deleted = false ");

        String sqlOrder = " order by (CASE when ac.progressStatus = 'UNCOMPLETED' then 0 when" +
                " ac.progressStatus = 'NOT_STARTED' then 1 when ac.progressStatus = 'COMPLETED' then 2 end)";

        Query query;

        if(!Objects.isNull(beforeDate) && Objects.isNull(afterDate)){
            querySql.append("and (ac.assignDate < :beforeDate and ac.assignDate is not null)");
            querySql.append(sqlOrder);
            query = entityManager.createQuery(querySql.toString());
            query.setParameter("beforeDate", beforeDate);
        }
        else if(!Objects.isNull(afterDate) && Objects.isNull(beforeDate)){
            querySql.append("and (ac.assignDate > :afterDate and ac.assignDate is not null)");
            querySql.append(sqlOrder);
            query = entityManager.createQuery(querySql.toString());
            query.setParameter("afterDate",afterDate);
        }
        else if(!Objects.isNull(beforeDate) && !Objects.isNull(afterDate)){
            querySql.append("and (ac.assignDate between :afterDate and :beforeDate)");
            querySql.append(sqlOrder);
            query = entityManager.createQuery(querySql.toString());
            query.setParameter("beforeDate", beforeDate);
            query.setParameter("afterDate",afterDate);
        } else{
            querySql.append(sqlOrder);
            query = entityManager.createQuery(querySql.toString());
        }

        //get total
        List<CourseHistoryDTO> listAll = (List<CourseHistoryDTO>)  query
                .setParameter("userId", userId).setParameter("assignType",courseType)
                .setParameter("courseName",courseName).setParameter("progressStatus", progressStatus)
                .getResultList();
        int total = listAll.size();

        query.setFirstResult((paging.getPageNumber()) * paging.getPageSize());
        query.setMaxResults(paging.getPageSize());

        List<CourseHistoryDTO> courseHistoryDTOS = (List<CourseHistoryDTO>)  query
                .setParameter("userId", userId).setParameter("assignType",courseType)
                .setParameter("courseName",courseName)
                .getResultList();

        return new PageImpl<>(courseHistoryDTOS, paging,total);
    }

    @Override
    public Page<CourseDTO> getCourseFilter(Integer pageNo, Integer pageSize, String name, List<String> topics, List<String> experiences, List<String> skills) {
        pageNo = pageNo <= 0 ? 0 : pageNo - 1;
        Pageable paging = PageRequest.of(pageNo, pageSize);
        if (name != null) {
            name = name.trim();
        }
        List<Course> courses = courseRepository.getListCourseFilter(name);
        Set<Course> suitableCourses = new HashSet<>();
        for (Course course : courses) {
            boolean isOK = true;
            if (Objects.nonNull(skills) && !skills.isEmpty()) {
                Set<String> courseSkillsName = course.getSkills().stream().map(Skill::getName).collect(Collectors.toSet());
                isOK = filterCourse(skills, courseSkillsName);
            }
            if (Objects.nonNull(topics) && !topics.isEmpty() && isOK) {
                String courseCategoryTraining = course.getCategoryTraining().getName();
                isOK = topics.contains(courseCategoryTraining);
            }
            if (Objects.nonNull(experiences) && !experiences.isEmpty() && isOK) {
                Set<String> courseExpsName = course.getExperiences().stream().map(Experience::getName).collect(Collectors.toSet());
                isOK = filterCourse(experiences, courseExpsName);
            }
            if (isOK) {
                suitableCourses.add(course);
            }
        }

        List<Course> sortedCourses = suitableCourses
                .stream()
                .sorted(Collections.reverseOrder(Comparator.comparing(Course::getCreatedDate)))
                .collect(Collectors.toList());
        List<CourseDTO> suitableCourseDTOs = sortedCourses.stream().map(course -> modelMapper.map(course, CourseDTO.class)).collect(Collectors.toList());

        int totalPages = suitableCourseDTOs.size() / pageSize;

        int max = pageNo >= totalPages ? suitableCourseDTOs.size() : pageSize * (pageNo + 1);
        int min = pageNo > totalPages ? max : pageSize * pageNo;
        List<CourseDTO> returnCourses = suitableCourseDTOs.subList(min, max);
        returnCourses.forEach(courseDTO -> {
            //courseDTO.setTotalLessons(lessonStructureRepository.getTotalLessonByCourseId(courseDTO.getId()));
            courseDTO.setPathPreview(courseDTO.getPathPreview());
        });
        return new PageImpl<>(returnCourses, paging, suitableCourses.size());
    }

    public ProcessUserCourseResponse getProcessUserCourses(Long userId) {
        int totalCompletedCourse = assignCourseRepository.getTotalUserCourseByStatus(userId, StatusCourseEnum.COMPLETED);
        int totalCourse = assignCourseRepository.getTotalUserCourse(userId);
        int totalNotStartedCourse = assignCourseRepository.getTotalUserCourseByStatus(userId, StatusCourseEnum.NOT_STARTED);
        int totalUncompletedCourse = assignCourseRepository
                .getTotalUserCourseByStatus(userId, StatusCourseEnum.UNCOMPLETED);

        int totalCertifications = assignCourseRepository.getTotalCertification(userId);

        int totalAwardedCertifications = assignCourseRepository
                .getTotalAwardedCertifications(userId, StatusCourseEnum.COMPLETED);

        return new ProcessUserCourseResponse(totalCourse, totalCompletedCourse,
                totalUncompletedCourse, totalNotStartedCourse, totalCertifications, totalAwardedCertifications);
    }

    private boolean filterCourse(List<String> filterConditions, Set<String> filters) {
        boolean isOK = true;
        for (String filter : filterConditions) {
            isOK = filters.contains(filter);
            if (isOK) {
                break;
            }
        }
        return isOK;
    }

    private String calculateLessonProgress(int total, int complete) {
        if (complete == 0) {
            return "0%";
        } else {
            Double completeProgress = (((double) complete / total)) * 100;
            return completeProgress.intValue() + "%";
        }
    }

    @Override
    public ProgressUserCourseResponse getProgressCoursesUser(String username, Long courseId) {
        // Lấy user id từ context
        Long userId = getAppUserIdByUserName(username);

        // Kiểm tra xem user có đang học khóa học không
        boolean isExistsCourseOfUser = assignCourseRepository.existsAssignCourseByAppUser_IdAndCourse_Id(userId, courseId);
        if (!isExistsCourseOfUser)
            return new ProgressUserCourseResponse(0, 0, false,
                    0,0);

        //in case admin add or remove lesson -> progress and status of assign course will be conflict
        // this is the temporary way to resolve this problem
        AssignCourse assignCourse = assignCourseRepository.getAssignCourseByCourseIdAndAppUserIdAndDeletedFalse(courseId,userId);

        int totalCompletedLessonOfCourse = assignCourseRepository.getTotalUserCoursesByStatus(userId, courseId);
        int totalLessonOfCourse = assignCourseRepository.getTotalUserCourses(courseId);

        int totalContent = courseRepository.getTotalContentUpload(courseId);

        int totalCompletedContentUpload = lessonContentUploadHistoryRepository.getTotalCompletedContentUpload(
                courseId,userId);
        if(assignCourse.getProgressStatus().equals(StatusCourseEnum.COMPLETED)){
            return new ProgressUserCourseResponse(totalLessonOfCourse, totalCompletedLessonOfCourse, true,
                    totalContent,totalCompletedContentUpload);
        }
        return new ProgressUserCourseResponse(totalLessonOfCourse, totalCompletedLessonOfCourse,
                false, totalContent, totalCompletedContentUpload);
    }

    @Override
    public Page<CourseDTO> getHighRatingCourse(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<CourseDTO> courses = courseRepository.findHighRatingCourses(pageable);
        return courses;
    }

    private Long getAppUserIdByUserName(String username) {
        return userRepository.findIdByUsername(username);
    }
}
