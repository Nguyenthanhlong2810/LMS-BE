package ntlong.service.impl;

import ntlong.dto.ExperienceDTO;
import ntlong.model.Experience;
import ntlong.payload.response.ExperienceSearchResponse;
import ntlong.repository.CourseRepository;
import ntlong.repository.ExperienceRepository;
import ntlong.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExperienceServiceImpl implements ExperienceService {
    private final ExperienceRepository experienceRepository;
    private final ModelMapper modelMapper;

    private final CourseRepository courseRepository;
    @Override
    public List<ExperienceSearchResponse> findAllSearch(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset - 1, limit);
        Page<Experience> experiences = experienceRepository.findAllByDeletedFalse(pageable);
        List<ExperienceSearchResponse> experienceDTOs = new ArrayList<>();
        for(Experience experience : experiences){
            ExperienceSearchResponse experienceDTO = new ExperienceSearchResponse();
            experienceDTO.setName(experience.getName());
            experienceDTO.setTotalCourse(courseRepository.getTotalCourseHasExperience(experience.getName()));
            if(experienceDTO.getTotalCourse() > 0) {
                experienceDTOs.add(experienceDTO);
            }
        }
        return experienceDTOs;
    }

    @Override
    public List<Experience> findAllExp() {
        return experienceRepository.findAll();
    }

    @Override
    public List<ExperienceDTO> findAllSearch(String name, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset - 1, limit);
        Page<Experience> experiences = experienceRepository.findByName(name, pageable);
        List<ExperienceDTO> experienceDTOS = new ArrayList<>();
        if (experiences != null)
            experienceDTOS = experiences.stream().map(experience -> modelMapper.map(experience, ExperienceDTO.class)).collect(Collectors.toList());
        return experienceDTOS;
    }
}
