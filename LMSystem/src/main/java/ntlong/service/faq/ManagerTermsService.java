package ntlong.service.faq;

import ntlong.model.faq.ManagerTerms;
import ntlong.payload.request.faq.ManagerTermsRequest;
import ntlong.payload.response.faq.ResponseDTO;

import java.util.List;

public interface ManagerTermsService {

    ManagerTerms getAllTerms();
    ManagerTerms findOne(Long id);
    ResponseDTO createdTerms(ManagerTermsRequest request);
    ResponseDTO updateTerms(Long id, ManagerTermsRequest request);
    ResponseDTO updateDeleteListTerms(List<Long> ids);
}
