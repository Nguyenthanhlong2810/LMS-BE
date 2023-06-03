package ntlong.service;

import ntlong.dto.SkillDTO;
import ntlong.model.Skill;
import ntlong.payload.response.SkillSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SkillService {

    Page<Skill> getAllSkill(Pageable pageable);

    List<SkillSearchResponse> getAllSkill(int offset, int limit);
}
