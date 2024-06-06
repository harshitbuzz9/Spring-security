package com.bridge.herofincorp.service.impls;

import com.bridge.herofincorp.configs.APILogger;
import com.bridge.herofincorp.exceptions.AssociateNotFoundException;
import com.bridge.herofincorp.exceptions.DatabaseAccessException;
import com.bridge.herofincorp.exceptions.PhoneNumberAlreadyExistException;
import com.bridge.herofincorp.model.entities.Associate;
import com.bridge.herofincorp.model.entities.AssociateRole;
import com.bridge.herofincorp.model.entities.Role;
import com.bridge.herofincorp.model.request.AssociateRequest;
import com.bridge.herofincorp.model.request.AssociateUpdateRequest;
import com.bridge.herofincorp.model.request.DeleteRequest;
import com.bridge.herofincorp.model.response.AssociateResponse;
import com.bridge.herofincorp.model.response.PartnerResponse;
import com.bridge.herofincorp.repository.AssociateRepository;
import com.bridge.herofincorp.repository.AssociateRoleRepository;
import com.bridge.herofincorp.service.AssociateService;
import com.bridge.herofincorp.service.DealerService;
import com.bridge.herofincorp.utils.ApplicationConstants;
import com.bridge.herofincorp.utils.Security;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.file.LinkOption;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class AssociateServiceImpl implements AssociateService {
    @Autowired
    private AssociateRepository repository;
    @Autowired
    private AssociateRoleRepository roleRepository;
    @Autowired
    private DealerService dealerService;
    @Autowired
    private ModelMapper mapper;
    @Override
    public AssociateResponse addAssociate(AssociateRequest request, APILogger logger) {
        logger.add("AssociateService.addAssociate()-startTime: "+LocalDateTime.now(),"");
        logger.add("getting current logged-in user","");
        final String currentUser = Security.getCurrentUser();
        logger.add("current logged-in user: "+currentUser,"");
        logger.add("getting dealer Information for current logged-in user","");
        PartnerResponse dealer = dealerService.getDealerInfo(currentUser);
        logger.add("Found dealer: "+dealer,"");
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Optional<Associate> optionalAssociate;
        try{
            logger.add("getting associate information with phone number: "+request.getPhone(),"");
            optionalAssociate = repository.findByPhone(request.getPhone());
        }catch(DataAccessException e){
            logger.logError(e.getMessage(),HttpStatus.BAD_REQUEST.value());
            throw new DatabaseAccessException("Error accessing the database");
        }
        if (optionalAssociate.isPresent()) throw new PhoneNumberAlreadyExistException("Phone No. already exist");
        Associate associate = Associate.builder()
                .name(request.getName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .status(ApplicationConstants.STAFF_STATUS_ACTIVE)
                .product(dealer.getProduct())
                .dealerCode(currentUser)
                .created(currentTime)
                .updated(currentTime)
                .createdBy(currentUser)
                .updatedBy(currentUser)
                .build();
        Associate savedAssociate;
        try{
            logger.add("saving associate data","");
            savedAssociate = repository.save(associate);
            logger.add("saved associate: "+savedAssociate,"");
        }catch(DataAccessException e){
            logger.logError(e.getMessage(),HttpStatus.BAD_REQUEST.value());
            throw new DatabaseAccessException("Error accessing the database");
        }
        request.getRole()
                .forEach(x->{
                    AssociateRole role = AssociateRole.builder()
                            .staffId(savedAssociate.getStaffId())
                            .role(x)
                            .created(currentTime)
                            .createdBy(savedAssociate.getDealerCode())
                            .updated(currentTime)
                            .updatedBy(savedAssociate.getDealerCode())
                            .build();
                    try{
                        logger.add("saving role data for associateId: "+savedAssociate.getStaffId(),"");
                        roleRepository.save(role);
                        logger.add("role data saved","");
                    }catch(DataAccessException e){
                        logger.logError(e.getMessage(),HttpStatus.BAD_REQUEST.value());
                        logger.add("rollbacking associate data from the database with staff id: "+savedAssociate.getStaffId(),"");
                        repository.deleteById(savedAssociate.getStaffId());
                        throw new DatabaseAccessException("Error accessing the database");
                    }
                });
        List<AssociateRole> roles;
        try{
            logger.add("fetching roles data with staffId: "+savedAssociate.getStaffId(),"");
            roles = roleRepository.findAllByStaffId(savedAssociate.getStaffId());
            logger.add("roles fetched: "+roles,"");
        }catch(DataAccessException e){
            logger.logError(e.getMessage(),HttpStatus.BAD_REQUEST.value());
            throw new DatabaseAccessException("Error accessing the database");
        }
        logger.add("AssociateService.addAssociate()-endTime: "+LocalDateTime.now(),"");
        return AssociateResponse.builder()
                .staffId(savedAssociate.getStaffId())
                .name(savedAssociate.getName())
                .lastName(savedAssociate.getLastName())
                .dealerCode(savedAssociate.getDealerCode())
                .email(savedAssociate.getEmail())
                .phone(savedAssociate.getPhone())
                .role(roles.stream().map(AssociateRole::getRole).toList())
                .status(savedAssociate.getStatus())
                .product(savedAssociate.getProduct())
                .build();
    }

    @Override
    public AssociateResponse updateAssociate(Integer staffId, AssociateUpdateRequest request, APILogger logger) {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        logger.add("AssociateService.updateAssociate()-startTime"+LocalDateTime.now(),"");
        Associate associate;
        List<AssociateRole> roles = new ArrayList<>();
        try{
            logger.add("fetching associate data with staffId: "+staffId,"");
            associate = repository.findById(staffId)
                    .orElseThrow(()->new AssociateNotFoundException("Associate doesn't exist with associateId: "+staffId));
            logger.add("found associate: "+associate,"");
        }catch(DataAccessException e){
            logger.logError(e.getMessage(),HttpStatus.FORBIDDEN.value());
            throw new DatabaseAccessException("Error accessing the database");
        }
        associate.setName(request.getName());
        associate.setLastName(request.getLastName());
        associate.setEmail(request.getEmail());
        associate.setStatus(request.getStatus());
        associate.setUpdated(currentTime);
        associate.setUpdatedBy(Security.getCurrentUser());
        try{
            Associate savedAssociate = repository.save(associate);
            logger.add("updated associate: "+associate,"");
            if(!request.getRole().isEmpty()){
                logger.add("deleting roles data with staffId: "+staffId,"");
                roleRepository.deleteByStaffId(savedAssociate.getStaffId());
                request.getRole()
                        .forEach(x->{
                            AssociateRole role = AssociateRole.builder()
                                    .staffId(savedAssociate.getStaffId())
                                    .role(x)
                                    .created(currentTime)
                                    .createdBy(savedAssociate.getDealerCode())
                                    .updated(currentTime)
                                    .updatedBy(savedAssociate.getDealerCode())
                                    .build();
                            try{
                                logger.add("updating roles data with staffId: "+staffId,"");
                                roleRepository.save(role);
                                logger.add("role data saved successfully","");
                            }catch(DataAccessException e){
                                logger.logError(e.getMessage(),HttpStatus.BAD_REQUEST.value());
                                logger.add("rollbacking associate data from the database with staff id: "+associate.getStaffId(),"");
                                repository.deleteById(savedAssociate.getStaffId());
                                logger.add("rollback successful for staff id: "+associate.getStaffId(),"");
                                throw new DatabaseAccessException("Error accessing the database");
                            }
                        });
            }
            roles = roleRepository.findAllByStaffId(staffId);
        }catch(DataAccessException e){
            logger.logError(e.getMessage(),HttpStatus.BAD_REQUEST.value());
            throw new DatabaseAccessException("Error accessing the database");
        }
        logger.add("AssociateService.updateAssociate()-endTime"+LocalDateTime.now(),"");
        AssociateResponse response = mapper.map(associate,AssociateResponse.class);
        response.setRole(roles.stream().map(AssociateRole::getRole).toList());
        return response;
    }

    @Override
    public AssociateResponse deleteAssociate(Integer associateId, DeleteRequest request, APILogger logger) {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        logger.add("AssociateService.deleteAssociate()-startTime: "+LocalDateTime.now(),"");
        Associate associate;
        List<AssociateRole> roles;
        try{
            logger.add("fetching associate data for deletion with staffId: "+associateId,"");
            associate = repository.findById(associateId)
                    .orElseThrow(()->new AssociateNotFoundException("Associate doesn't exist with associateId: "+associateId));
            logger.add("found associate: "+associate,"");
            logger.add("fetching roles data for deletion with staffId: "+associateId,"");
            roles = roleRepository.findAllByStaffId(associateId);
            logger.add("Found roles: "+roles,"");
        }catch(DataAccessException e){
            logger.logError(e.getMessage(),HttpStatus.BAD_REQUEST.value());
            throw new DatabaseAccessException("Error accessing the database");
        }
        associate.setStatus(request.getStatus());
        associate.setUpdated(currentTime);
        associate.setUpdatedBy(Security.getCurrentUser());
        try{
            logger.add("deleting associate data for staffId: "+associateId,"");
            repository.save(associate);
            logger.add("deleted associate: "+associate,"");
        }catch(DataAccessException e){
            logger.logError(e.getMessage(),HttpStatus.BAD_REQUEST.value());
            throw new DatabaseAccessException("Error accessing the database");
        }
        logger.add("AssociateService.deleteAssociate()-endTime: "+LocalDateTime.now(),"");
        AssociateResponse response = mapper.map(associate,AssociateResponse.class);
        response.setRole(roles.stream().map(AssociateRole::getRole).toList());
        return response;
    }

    @Override
    public List<Role> getAllRoles(APILogger logger) {
        List<Role> roles = Arrays.asList(Role.values());
        logger.add("AssociateService.getAllRoles() data: "+roles,"");
        return roles;
    }

    @Override
    public List<AssociateResponse> getAllAssociates(APILogger logger) {
        logger.add("AssociateService.getAllAssociates-startTime",LocalDateTime.now().toString());
        List<Associate> associates;
        try{
            logger.add("calling AssociateRepository to fetch all associates","");
            associates = repository.findAll();
            logger.add("associates fetched: "+associates,"");
        }catch(DataAccessException e){
            logger.logError(e.getMessage(),HttpStatus.BAD_REQUEST.value());
            throw new DatabaseAccessException("Error accessing the database");
        }
        logger.add("AssociateService.getAllAssociates-endTime",LocalDateTime.now().toString());
        return associates.stream().map(x->{
            List<AssociateRole> role;
            try{
                logger.add("fetching role data in getAllAssociates() with staffId: "+x.getStaffId(),"");
                role = roleRepository.findAllByStaffId(x.getStaffId());
                logger.add("roles fetched: "+role,"");
            }catch(DataAccessException e){
                logger.logError(e.getMessage(),HttpStatus.BAD_REQUEST.value());
                throw new DatabaseAccessException("Error accessing the database");
            }
            AssociateResponse response = new AssociateResponse();
            response.setStaffId(x.getStaffId());
            response.setName(x.getName());
            response.setLastName(x.getLastName());
            response.setPhone(x.getPhone());
            response.setEmail(x.getEmail());
            response.setDealerCode(x.getDealerCode());
            response.setStatus(x.getStatus());
            response.setProduct(x.getProduct());
            response.setRole(role.stream().map(AssociateRole::getRole).toList());
            return response;
        }).toList();
    }

    @Override
    public AssociateResponse getAssociateByPhone(APILogger log, String phone){
        List<AssociateRole> roles;
        AssociateResponse response;
        try{
//            log.add("getAssociateByPhone,getting associate with phone number: "+phone,"");
            Associate associate = repository.findByPhone(phone).orElseThrow(()->new AssociateNotFoundException("Associate doesn't exist with Phone Number: "+phone));
            roles = roleRepository.findAllByStaffId(associate.getStaffId());
            response = mapper.map(associate,AssociateResponse.class);
        }catch(DataAccessException e){
            throw new DatabaseAccessException("Error accessing the database");
        }
        response.setRole(roles.stream().map(AssociateRole::getRole).toList());
        return response;
    }

    @Override
    public AssociateResponse getAssociateById(Integer id, APILogger logger){
        List<AssociateRole> roles;
        AssociateResponse response;
        try{
            logger.add("getAssociateById-startTime", LocalDateTime.now().toString());
            logger.add("calling AssociateRepository to get associate with id: "+id,"");
            Associate associate = repository.findById(id).orElseThrow(()->{
                logger.logError("Associate not found",HttpStatus.NOT_FOUND.value());
                return new AssociateNotFoundException("Associate doesn't exist with associateId: " + id);
            });
            logger.add("AssociateService.getAssociateById() data: "+associate,"");
            logger.add("calling RoleRepository to get Role data for associate with id: "+id,"");
            roles = roleRepository.findAllByStaffId(id);
            logger.add("roleRepository.findAllByStaffId() data: "+roles,"");
            response = mapper.map(associate,AssociateResponse.class);
            logger.add("getAssociateById-endTime", LocalDateTime.now().toString());
        }catch(DataAccessException e){
            logger.logError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            throw new DatabaseAccessException("Error accessing the database");
        }
        response.setRole(roles.stream().map(AssociateRole::getRole).toList());
        return response;
    }
}
