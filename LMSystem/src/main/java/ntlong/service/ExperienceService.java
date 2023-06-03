package ntlong.service;

import ntlong.dto.ExperienceDTO;
import ntlong.model.Experience;
import ntlong.payload.response.ExperienceSearchResponse;

import java.util.List;

public interface ExperienceService {
    List<ExperienceSearchResponse> findAllSearch(int offset, int limit);

    List<Experience> findAllExp();

    List<ExperienceDTO> findAllSearch(String name, int offset, int limit);
}
