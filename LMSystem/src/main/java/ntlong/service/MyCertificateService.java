package ntlong.service;

import ntlong.dto.MyCertificateDTO;
import ntlong.payload.response.CertificateResponse;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface MyCertificateService {

    Page<MyCertificateDTO> getMyCertificatesFilter(String username, Integer pageNo, Integer pageSize, String keyword);

    MyCertificateDTO findMyCertificateDetail(String username, Long courseId) throws Exception;

    void downloadMyCertificateDetailByType(String username, Long courseId, String type, HttpServletResponse response) throws IOException;

    void downloadCertificatesAsZip(String username, List<Long> courseIds, String type, HttpServletResponse response) throws Exception;

    CertificateResponse getPreviewCertificate(Long courseId, String username);

    CertificateResponse createCertificate(Long courseId, String username);
}
