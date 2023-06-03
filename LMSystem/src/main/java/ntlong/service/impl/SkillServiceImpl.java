package ntlong.service.impl;

import ntlong.dto.SkillDTO;
import ntlong.model.Skill;
import ntlong.payload.response.SkillSearchResponse;
import ntlong.repository.CourseRepository;
import ntlong.repository.SkillRepository;
import ntlong.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final Environment env;
    private final SkillRepository skillRepository;
    private final ModelMapper modelMapper;

    private final CourseRepository courseRepository;

    @Override
    public Page<Skill> getAllSkill(Pageable pageable) {
        return skillRepository.findAllWithPagination(pageable);
    }


    @Override
    public List<SkillSearchResponse> getAllSkill(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset - 1, limit);
        Page<Skill> skills = skillRepository.findAllByDeletedFalse(pageable);
        List<SkillSearchResponse> skillNameDTOS = new ArrayList<>();
        if (skills.hasContent()) {
            for(Skill skill : skills) {
                SkillSearchResponse skillDTO = new SkillSearchResponse();
                skillDTO.setName(skill.getName());
                int totalCourse = courseRepository.getTotalCourseHasSkill(skill.getId());
                skillDTO.setTotalCourse(totalCourse);
                if(skillDTO.getTotalCourse() > 0) {
                    skillNameDTOS.add(skillDTO);
                }
            }
        }
        return skillNameDTOS;
    }
}
