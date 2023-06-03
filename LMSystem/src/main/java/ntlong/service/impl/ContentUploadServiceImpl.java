package ntlong.service.impl;

import ntlong.dto.ContentUploadDTO;
import ntlong.dto.UploadFileDTO;
import ntlong.enums.TypeContentUploadEnum;
import ntlong.exception.BusinessException;
import ntlong.exception.CustomException;
import ntlong.exception.ResourceNotFoundException;
import ntlong.model.ContentUpload;
import ntlong.model.LessonContentUpload;
import ntlong.repository.*;
import ntlong.service.AmazonClient;
import ntlong.service.ContentUploadService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentUploadServiceImpl implements ContentUploadService {
    private final ContentUploadRepository contentUploadRepository;
    private final AmazonClient amazonClient;

    private final NoteLessonRepository noteLessonRepository;

    private final LessonContentUploadRepository lessonContentUploadRepository;

    private final LessonContentUploadHistoryRepository lessonContentUploadHistoryRepository;
    
    private final ModelMapper modelMapper;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    @Transactional
    public List<ContentUploadDTO> createContent(ContentUploadDTO contentUploadDTO, MultipartFile[] files) throws IOException {
        validateCreateContent(contentUploadDTO, files);
        validateDuration(contentUploadDTO);

            UploadFileDTO uploadFileDTO = amazonClient.uploadMultiFile(files);
            if (Objects.isNull(uploadFileDTO)) {
                throw new CustomException("Upload content file null", HttpStatus.BAD_REQUEST);
            }
            List<ContentUpload> contentUploads = new ArrayList<>();
            uploadFileDTO.getFiles().forEach(item -> {
                ContentUpload contentUpload = modelMapper.map(contentUploadDTO, ContentUpload.class);
                contentUpload.setLinkFileContent(item.getPreviewUrl());
                contentUpload.setNameContent(item.getFileName());
                contentUploads.add(contentUpload);
            });
            log.info("Create content with total {}", uploadFileDTO.getTotalFiles());
            List<ContentUpload> contentsUploaded = contentUploadRepository.saveAll(contentUploads);

            return contentsUploaded.stream().map(item -> {
                ContentUploadDTO contentUploadedDTO = modelMapper.map(item, ContentUploadDTO.class);
                contentUploadedDTO.setLinkFileContent( item.getLinkFileContent());
                return contentUploadedDTO;
            }).collect(Collectors.toList());
    }

    private void validateCreateContent(ContentUploadDTO contentUploadDTO, MultipartFile[] files) {
        if (contentUploadDTO.getType() == null) {
            throw new CustomException("Type is required", HttpStatus.BAD_REQUEST);
        }
        if (files.length == 0) {
            throw new CustomException("File content upload null", HttpStatus.BAD_REQUEST);
        }

    }

    private void validateDuration(ContentUploadDTO contentUploadDTO) {
        if (contentUploadDTO.getType() == TypeContentUploadEnum.VIDEO) {
            String regex = "";
            String longTime = contentUploadDTO.getTimeLong();
            if (longTime.contains("h")) {
                regex += "((\\d+)h)";
            }
            if (longTime.contains("m")) {
                regex += "((\\d+)m)";
            }
            if (longTime.contains("s")) {
                regex += "((\\d+)s)";
            }
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(longTime);
            if (!matcher.matches() || regex.equals("")) {
                throw new CustomException("Thời lượng không đúng định dạng", HttpStatus.BAD_REQUEST);
            }
        }
    }

    @Override
    public ContentUploadDTO updateContent(ContentUploadDTO contentUploadDTO) throws ResourceNotFoundException {
        if (contentUploadDTO.getId() == null || contentUploadDTO.getId() == 0) {
            throw new ResourceNotFoundException("Does not existed id content upload :" + contentUploadDTO.getId());
        }
        if(!contentUploadRepository.existsById(contentUploadDTO.getId()))
            throw new ResourceNotFoundException("Content does not existed with id:" + contentUploadDTO.getId());

        ContentUpload contentUpload1 = modelMapper.map(contentUploadDTO, ContentUpload.class);
        contentUpload1.setLinkFileContent(contentUpload1.getLinkFileContent());
        contentUpload1 = contentUploadRepository.save(contentUpload1);
        ContentUploadDTO contentUploadRes = modelMapper.map(contentUpload1, ContentUploadDTO.class);
        contentUploadRes.setLinkFileContent( contentUpload1.getLinkFileContent());
        return contentUploadRes;
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) throws BusinessException {
        List<Long> contentUploadIds = lessonContentUploadRepository.findContentUploadIdList();
        for (Long id : ids) {
            if (!contentUploadRepository.existsById(id)) {
                throw new BusinessException("Không tồn tại bản ghi với id: " + id, HttpStatus.BAD_REQUEST);
            }
            if (contentUploadIds.contains(id)) {
                throw new BusinessException("Những bản ghi ở trạng thái Đang hoạt động sẽ không được phép xoá", HttpStatus.BAD_REQUEST);
            }
            List<LessonContentUpload> lessonContentUploads = lessonContentUploadRepository.findLessonContentUploadsByContentUploadId(id);
            lessonContentUploadHistoryRepository.deleteAllByLessonContentUploadId(lessonContentUploads
                    .parallelStream()
                    .map(LessonContentUpload::getId)
                    .collect(Collectors.toList()));
            lessonContentUploadRepository.deleteAllById(lessonContentUploads
                    .parallelStream()
                    .map(LessonContentUpload::getId)
                    .collect(Collectors.toList()));
        }
        noteLessonRepository.deleteAllByContentUploadId(ids);
        contentUploadRepository.deleteAllById(ids);
    }

    @Override
    public Page<ContentUploadDTO> searchContenUploads(Integer pageNo, Integer pageSize, String keySearch, TypeContentUploadEnum type, String sortBy) {
        Pageable paging = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Order.desc(sortBy)));
        Page<ContentUpload> contentUploadPage = contentUploadRepository.searchContentUpload(paging, keySearch, type);
        Page<ContentUploadDTO> contentUploadDTOPage = contentUploadPage.map(item -> {
            ContentUploadDTO contentUploadDTO = modelMapper.map(item, ContentUploadDTO.class);
            contentUploadDTO.setLinkFileContent( item.getLinkFileContent());
            return contentUploadDTO;
        });
        List<Long> contentUploadIds = lessonContentUploadRepository.findContentUploadIdList();
        if (contentUploadDTOPage.hasContent()) {
            contentUploadDTOPage.getContent().forEach(contentUploadDTO -> contentUploadDTO.setStatusUsed(contentUploadIds.contains(contentUploadDTO.getId())));
        }
        return contentUploadDTOPage;
    }

    @Override
    public ContentUploadDTO findById(Long id) throws ResourceNotFoundException {
        ContentUpload contentUpload = contentUploadRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Id Content Upload does not existed"));
        ContentUploadDTO contentUploadDTO = modelMapper.map(contentUpload, ContentUploadDTO.class);
        contentUploadDTO.setLinkFileContent( contentUpload.getLinkFileContent());
        return contentUploadDTO;
    }
}
