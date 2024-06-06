package com.bridge.herofincorp.controller;

import com.bridge.herofincorp.configs.APILogger;
import com.bridge.herofincorp.exceptions.AssociateNotFoundException;
import com.bridge.herofincorp.exceptions.PhoneNumberAlreadyExistException;
import com.bridge.herofincorp.model.entities.Role;
import com.bridge.herofincorp.model.request.AssociateRequest;
import com.bridge.herofincorp.model.request.AssociateUpdateRequest;
import com.bridge.herofincorp.model.request.DeleteRequest;
import com.bridge.herofincorp.model.response.AssociateResponse;
import com.bridge.herofincorp.service.AssociateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/bridge-app/associate")
public class AssociateController {

    @Autowired
    private AssociateService service;

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/addAssociate")
    public AssociateResponse addAssociate(@RequestAttribute(name = "logger") APILogger logger, @RequestBody AssociateRequest request){
        AssociateResponse response;
        try{
            logger.add("AssociateController.addAssociate()-startTime: "+LocalDateTime.now(),"");
            response = service.addAssociate(request,logger);
            logger.add("AssociateController.addAssociate()-endTime: "+LocalDateTime.now(),"");
            logger.logSuccess(200);
        }catch (DataAccessException e){
            logger.logError(e.getMessage(),HttpStatus.BAD_REQUEST.value());
            throw e;
        }catch (PhoneNumberAlreadyExistException e){
            logger.logError(e.getMessage(),HttpStatus.CONFLICT.value());
            throw e;
        }
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/updateAssociate/{staffId}")
    public AssociateResponse updateAssociate(@RequestAttribute("logger") APILogger logger,@PathVariable Integer staffId, @RequestBody AssociateUpdateRequest request){
        AssociateResponse response;
        try{
            logger.add("AssociateController.updateAssociate()-startTime: "+LocalDateTime.now(),"");
            response = service.updateAssociate(staffId,request,logger);
            logger.add("AssociateController.updateAssociate()-endTime: "+LocalDateTime.now(),"");
        }catch (DataAccessException e){
            logger.logError(e.getMessage(),HttpStatus.BAD_REQUEST.value());
            throw e;
        }catch (AssociateNotFoundException e){
            logger.logError(e.getMessage(),HttpStatus.NOT_FOUND.value());
            throw e;
        }
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/deleteAssociate/{staffId}")
    public AssociateResponse deleteAssociate(@RequestAttribute("logger") APILogger logger,@PathVariable Integer staffId, @RequestBody DeleteRequest request){
        AssociateResponse response;
        try{
            logger.add("AssociateController.deleteAssociate()-startTime: "+LocalDateTime.now(),"");
            response = service.deleteAssociate(staffId,request,logger);
            logger.add("AssociateController.deleteAssociate()-endTime: "+LocalDateTime.now(),"");
            logger.logSuccess(200);
        }catch (DataAccessException e){
            logger.logError(e.getMessage(),HttpStatus.BAD_REQUEST.value());
            throw e;
        }catch (AssociateNotFoundException e){
            logger.logError(e.getMessage(),HttpStatus.NOT_FOUND.value());
            throw e;
        }
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/getAllAssociates")
    public List<AssociateResponse> getAllAssociates(@RequestAttribute("logger") APILogger logger){
        List<AssociateResponse> associates;
        try{
            logger.add("AssociateController.getAllAssociates-startTime", LocalDateTime.now().toString());
            associates = service.getAllAssociates(logger);
            logger.add("AssociateController.getAllAssociates-endTime",LocalDateTime.now().toString());
            logger.logSuccess(200);
        }catch (DataAccessException e){
            logger.logError(e.getMessage(),HttpStatus.BAD_REQUEST.value());
            throw e;
        }
        return associates;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/getRoles")
    public List<Role> getAllRoles(@RequestAttribute("logger") APILogger logger){
        List<Role> roles;
        try{
            logger.add("calling Associate Service to get all roles","");
            logger.add("AssociateController.getAllRoles-startTime",LocalDateTime.now().toString());
            roles =  service.getAllRoles(logger);
            logger.add("AssociateController.getAllRoles-endTime",LocalDateTime.now().toString());
            logger.logSuccess(200);
        }catch (Exception e){
            logger.logError(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw e;
        }
        return roles;
    }
    @PreAuthorize("hasAnyRole('ADMIN','LEADGENERATION')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public AssociateResponse getAssociate(@RequestAttribute("logger") APILogger logger,@PathVariable Integer id){
        AssociateResponse response;
        try{
            logger.add("getAssociate-startTime", LocalDateTime.now().toString());
            logger.add("calling Associate Service to get Associate with id: "+id,"");
            response = service.getAssociateById(id,logger);
            logger.add("AssociateController.getAssociate() response"+response,"");
            logger.add("getAssociate-endTime", LocalDateTime.now().toString());
            logger.logSuccess(200);
        }catch (DataAccessException e){
            logger.logError(e.getMessage(),HttpStatus.BAD_REQUEST.value());
            throw e;
        }catch (AssociateNotFoundException e){
            logger.logError(e.getMessage(),HttpStatus.NOT_FOUND.value());
            throw e;
        }
        return response;
    }
}