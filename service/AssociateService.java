package com.bridge.herofincorp.service;

import com.bridge.herofincorp.configs.APILogger;
import com.bridge.herofincorp.model.entities.Role;
import com.bridge.herofincorp.model.request.AssociateRequest;
import com.bridge.herofincorp.model.request.AssociateUpdateRequest;
import com.bridge.herofincorp.model.request.DeleteRequest;
import com.bridge.herofincorp.model.response.AssociateResponse;

import java.util.List;

public interface AssociateService {
    AssociateResponse addAssociate(AssociateRequest request, APILogger logger);
    AssociateResponse updateAssociate(Integer staffId, AssociateUpdateRequest request, APILogger logger);
    AssociateResponse deleteAssociate(Integer associateId, DeleteRequest request, APILogger logger);
    List<Role> getAllRoles(APILogger logger);
    List<AssociateResponse> getAllAssociates(APILogger logger);
    AssociateResponse getAssociateByPhone(APILogger log, String phone);
    AssociateResponse getAssociateById(Integer id, APILogger logger);
}
