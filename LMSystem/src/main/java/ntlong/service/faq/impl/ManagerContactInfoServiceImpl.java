package ntlong.service.faq.impl;

import ntlong.exception.faq.CustomException;
import ntlong.exception.faq.EntityNotFoundException;
import ntlong.model.faq.ContactInfo;
import ntlong.payload.request.faq.ContactInfoRequest;
import ntlong.payload.response.faq.ResponseDTO;
import ntlong.repository.faq.ManagerContactInfoRepository;
import ntlong.service.faq.ManagerContactInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ManagerContactInfoServiceImpl implements ManagerContactInfoService {

    private ManagerContactInfoRepository contactInfoRepository;

    public ManagerContactInfoServiceImpl(ManagerContactInfoRepository contactInfoRepository) {
        this.contactInfoRepository = contactInfoRepository;
    }

    @Override
    public ContactInfo getContactInfo() {
        List<ContactInfo> contactInfoList = contactInfoRepository.findAllByDeletedFalse();
        if (contactInfoList.size() > 1){
            throw new CustomException("Contact Info greater than 2 records!!!", HttpStatus.CONFLICT);
        }
        if (contactInfoList.size() == 0){
            return new ContactInfo();
        }
        return contactInfoList.get(0);
    }

    @Override
    public ContactInfo findOne(Long id) {
        return contactInfoRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(ContactInfo.class,"Id",id.toString()));
    }

    @Override
    public ResponseDTO createdContactInfo(ContactInfoRequest request) {
        ContactInfo model = new ContactInfo();
        int check = checkDataContactInfoDetail(model, request, true);
        if (check == 200) {
            return new ResponseDTO("Created ContactInfo!","successfully!");
        }
        throw new CustomException("500 Error!", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseDTO updateContactInfo(Long id, ContactInfoRequest request) {
        ContactInfo model = contactInfoRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(ContactInfo.class,"Id",id.toString()));
        int check = checkDataContactInfoDetail(model, request, false);
        if (check == 200) {
            return new ResponseDTO("Updated ContactInfo!","successfully!");
        }
        throw new CustomException("500 Error!", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Transactional
    public int checkDataContactInfoDetail(ContactInfo model, ContactInfoRequest request, boolean isCreate) {
        if (!StringUtils.isNotBlank(request.getValueContact())) {
            throw new ntlong.exception.CustomException("Nội dung không được để trống",HttpStatus.BAD_REQUEST);
        }
        model.setValueContact(request.getValueContact());
        if (isCreate) {
            contactInfoRepository.deleteAll();
        }
        contactInfoRepository.save(model);
        return 200;
    }
}
