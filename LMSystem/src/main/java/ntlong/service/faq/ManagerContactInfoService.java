package ntlong.service.faq;

import ntlong.model.faq.ContactInfo;
import ntlong.payload.request.faq.ContactInfoRequest;
import ntlong.payload.response.faq.ResponseDTO;

public interface ManagerContactInfoService {

    ContactInfo getContactInfo();
    ContactInfo findOne(Long id);
    ResponseDTO createdContactInfo(ContactInfoRequest request);
    ResponseDTO updateContactInfo(Long id, ContactInfoRequest request);
}
