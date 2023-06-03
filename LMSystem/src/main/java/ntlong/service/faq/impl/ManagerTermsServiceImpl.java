package ntlong.service.faq.impl;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import ntlong.exception.faq.CustomException;
import ntlong.exception.faq.EntityNotFoundException;
import ntlong.model.faq.ManagerTerms;
import ntlong.payload.request.faq.ManagerTermsRequest;
import ntlong.payload.response.faq.ResponseDTO;
import ntlong.repository.faq.ManagerTermsRepository;
import ntlong.service.faq.ManagerTermsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@AllArgsConstructor
public class ManagerTermsServiceImpl implements ManagerTermsService {

    private final ManagerTermsRepository managerTermsRepository;

    private final ModelMapper model;


    @Override
    public ManagerTerms getAllTerms() {
        List<ManagerTerms> managerTermsList = managerTermsRepository.findAllByDeletedFalse();
        if(managerTermsList.size() == 0){
            return new ManagerTerms();
        }
        return managerTermsList.get(0);
    }

    @Override
    public ManagerTerms findOne(Long id) {
        return managerTermsRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(ManagerTerms.class,"Id",id.toString()));
    }

    @Transactional
    @Override
    public ResponseDTO createdTerms(ManagerTermsRequest request) {
        managerTermsRepository.deleteAll();
        managerTermsRepository.save(model.map(request, ManagerTerms.class));

        return new ResponseDTO("Created terms!","successfully!");

    }

    @Override
    public ResponseDTO updateTerms(Long id, ManagerTermsRequest request) {
        ManagerTerms model = managerTermsRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(ManagerTerms.class,"Id",id.toString()));
        model.setValue(request.getValue());
        managerTermsRepository.save(model);
        return new ResponseDTO("Updated terms!","successfully!");
    }

    @Override
    public ResponseDTO updateDeleteListTerms(List<Long> ids) {
        if (ids.size() == 0) {
            throw new EntityNotFoundException("id",ids.toString());
        }
        List<ManagerTerms> managerTerms = managerTermsRepository.findAllById(ids);
        managerTermsRepository.saveAll(managerTerms);
        return new ResponseDTO("Delete multipart terms","Successfully!");
    }

}
