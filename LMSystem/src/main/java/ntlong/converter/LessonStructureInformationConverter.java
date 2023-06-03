package ntlong.converter;

import ntlong.dto.ContentUploadDTO;
import ntlong.dto.LessonStructureInformationDTO;
import ntlong.enums.TypeContentUploadEnum;
import ntlong.model.LessonContentUpload;
import ntlong.model.LessonStructure;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LessonStructureInformationConverter {
    private final ModelMapper modelMapper;
    private final ContentUploadConverter contentUploadConverter;

    public LessonStructureInformationDTO convertToDTO(LessonStructure lessonStructure) {
        LessonStructureInformationDTO lessonStructureDTO = modelMapper.map(lessonStructure, LessonStructureInformationDTO.class);
        if (!CollectionUtils.isEmpty(lessonStructure.getLessonContentUploads())) {
            lessonStructureDTO.setContentUploadIds(lessonStructure.getLessonContentUploads()
                    .stream().map(item -> item.getContentUpload().getId())
                    .collect(Collectors.toList()));
            lessonStructureDTO.setLessonCount(lessonStructure.getLessonContentUploads().size());
            // Processing get total duration
            Long contentUploadDurationTotal = lessonStructure.getLessonContentUploads()
                    .stream().mapToLong(LessonContentUpload::getDuration).sum();
            lessonStructureDTO.setTotalDuration(contentUploadDurationTotal);
            lessonStructureDTO.setContentUploads(lessonStructure.getLessonContentUploads()
                    .stream().map(item -> {
                        ContentUploadDTO contentUploadDTO = contentUploadConverter.convertToDTO(item.getContentUpload());
                        contentUploadDTO.setCanDownload(item.isCanDownload());
                        contentUploadDTO.setCompletedOpen(item.isCompletedOpen());
                        contentUploadDTO.setSortOrder(item.getSortOrder());
                        if(item.getContentUpload().getType().equals(TypeContentUploadEnum.VIDEO)) {
                            contentUploadDTO.setDuration(Objects.isNull(item.getDuration()) ? 0L : item.getDuration());
                        } else {
                            contentUploadDTO.setDuration(0L);
                        }
                        return contentUploadDTO;
                    })
                    .sorted(Comparator.comparingLong(ContentUploadDTO::getSortOrder))
                    .collect(Collectors.toList()));
        }

        lessonStructureDTO.setTotalDurationDisplay(convertSecondToHMSString(lessonStructureDTO.getTotalDuration()));
        return lessonStructureDTO;
    }

    public String convertSecondToHMSString(Long second) {
        Duration duration = Duration.ofSeconds(second);
        long HH = duration.toHours();
        long MM = duration.toMinutesPart();
        long SS = duration.toSecondsPart();
        String timeInHourMinuteSecond = "";
        if (HH > 0)
            timeInHourMinuteSecond = timeInHourMinuteSecond.concat(HH + "h");
        if (MM > 0)
            timeInHourMinuteSecond = timeInHourMinuteSecond.concat(" " + MM + "min");
        if (SS > 0)
            timeInHourMinuteSecond = timeInHourMinuteSecond.concat(" " + Long.toString(SS) + "s");
        return timeInHourMinuteSecond;
    }

    private Long convertTimeStampStringToLong(String timeS) {
        Long timeLong= 0L, h = 0L, m = 0L ,s = 0L;
        h = Long.parseLong(timeS.substring(0,2));
        m = Long.parseLong(timeS.substring(3,5));
        s = Long.parseLong(timeS.substring(6,8));
        timeLong = s + m * 60 + h * 3600;
        return timeLong;
    }

}
