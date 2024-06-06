package com.bridge.herofincorp.utils;

import com.bridge.herofincorp.model.dto.JourneyIngestionDTO;
import com.bridge.herofincorp.model.entities.LeadGeneration;
import com.bridge.herofincorp.model.request.LapRequest;
import com.bridge.herofincorp.model.request.LeadGenerationRequest;
import com.bridge.herofincorp.model.request.TwlRequest;
import com.bridge.herofincorp.model.response.LapELMApiResponse;
import com.bridge.herofincorp.model.response.LeadGenerationResponse;
import lombok.experimental.UtilityClass;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@UtilityClass
public class LeadGenerationMapper {

    public LeadGeneration mapRequestToLeadGenerationDetails(LeadGenerationRequest request, String dealerCode){
        return LeadGeneration.builder()
                .leadId(request.getApplicationId())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .mobileNumber(request.getMobileNumber())
                .dateOfBirth(request.getDob())
                .ageYrs(request.getAge())
                .gender(request.getGender())
                .addressLine1(request.getPermanentResiAddLine1())
                .addressLine2(request.getPermanentResiAddLine2())
                .addressLine3(request.getPermanentResiAddLine3())
                .landmark(request.getLandmark())
                .pincode(request.getPinCode())
                .dealerCode(dealerCode)
                .productCode(request.getProduct())
                .city(request.getCity())
                .state(request.getState())
                .loanAmount(request.getLoanAmount())
                .loanTenure(request.getTenure())
                .productCode(request.getProduct())
                .mobileNumber(request.getMobileNumber())
                .created(new Timestamp(System.currentTimeMillis()))
                .updated(new Timestamp(System.currentTimeMillis()))
                .consentStatus(false)
                .build();
    }
    public LeadGeneration mapJourneyRequestToLeadGenerationDetails(TwlRequest request, JourneyIngestionDTO response){
        return LeadGeneration.builder()
                .leadId(request.getPan()+ LocalDateTime.now())
                .consentStatus(true)
                .journeyId(String.valueOf(response.getData().getJourneyId()))
                .productCode(response.getData().getProduct())
                .offerCode(response.getData().getOfferCode())
                .leadStatus(response.getData().getJourneyStatus())
                .panNumberIndividual(request.getPan())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .dateOfBirth(LocalDate.parse(request.getDob()))
                .mobileNumber(request.getMobileNumber())
                .addressLine1(request.getCurrentResiAddLine1())
                .addressLine2(request.getCurrentResiAddLine2())
                .addressLine3(request.getCurrentResiAddLine3())
                .pincode(request.getCurrentResidentPin())
                .city(request.getCurrentResidentCity())
                .state(request.getCurrentResidentState())
                .model(request.getJourneyTwlDTO().getModel())
                .loanAmount(request.getJourneyTwlDTO().getAmountRequested())
                .loanTenure(request.getJourneyTwlDTO().getTenureSelected())
                .created(new Timestamp(System.currentTimeMillis()))
                .updated(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    public LeadGeneration mapJourneyRequestToLAPLeadGenerationDetails(LeadGenerationRequest request, LapELMApiResponse response){
        return LeadGeneration.builder()
                .leadId(request.getPan()+ LocalDateTime.now())
                .consentStatus(true)
                .journeyId(response.getId())
                .productCode(request.getProduct())
//                todo :response me offer code ni hai
                .offerCode(response.getId())
                .leadStatus(String.valueOf(response.isSuccess()))
                .panNumberIndividual(request.getPan())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .dateOfBirth((request.getDob()))
                .mobileNumber(request.getMobileNumber())
                .addressLine1(request.getPermanentResiAddLine1())
                .addressLine2(request.getPermanentResiAddLine2())
                .addressLine3(request.getPermanentResiAddLine3())
                .pincode(request.getPinCode())
                .city(request.getCity())
                .state(request.getState())
                .loanAmount(request.getLoanAmount())
                .loanTenure(request.getTenure())
                .companyAddress(request.getCompanyAddress())
                .panNumberCompany(request.getCompanyPan())
                .companyName(request.getCompanyName())
                .ownerName(request.getOwnerId())
                .dateOfIncorporation(LocalDate.parse(request.getDoIncorporation()))
                .constitution(request.getConstitution())
                .created(new Timestamp(System.currentTimeMillis()))
                .updated(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    public LeadGenerationResponse mapLeadToLeadGenerationResponse(LeadGeneration lead){
        return LeadGenerationResponse.builder().leadId(lead.getLeadId())
                .journeyId(lead.getJourneyId())
                .consentStatus(lead.getConsentStatus())
                .leadStatus(lead.getLeadStatus())
                .dealerCode(lead.getDealerCode())
                .staffId(lead.getStaffId())
                .productCode(lead.getProductCode())
                .offerCode(lead.getOfferCode())
                .panNumberIndividual(lead.getPanNumberIndividual())
                .panNumberCompany(lead.getPanNumberCompany())
                .companyName(lead.getCompanyName())
                .dateOfIncorporation(lead.getDateOfIncorporation())
                .companyAddress(lead.getCompanyAddress())
                .constitution(lead.getConstitution())
                .firstName(lead.getFirstName())
                .middleName(lead.getMiddleName())
                .lastName(lead.getLastName())
                .dateOfBirth(lead.getDateOfBirth())
                .ageYrs(lead.getAgeYrs())
                .gender(lead.getGender())
                .mobileNumber(lead.getMobileNumber())
                .residenceAddress(lead.getResidenceAddress())
                .contactDetails(lead.getContactDetails())
                .propertyAddress(lead.getPropertyAddress())
                .addressLine1(lead.getAddressLine1())
                .addressLine2(lead.getAddressLine2())
                .addressLine3(lead.getAddressLine3())
                .landmark(lead.getLandmark())
                .pincode(lead.getPincode())
                .city(lead.getCity())
                .state(lead.getState())
                .model(lead.getModel())
                .loanAmount(lead.getLoanAmount())
                .loanTenure(lead.getLoanTenure())
                .ownerName(lead.getOwnerName())
                .residenceType(lead.getResidenceType())
                .occupationType(lead.getOccupationType())
                .mthsInCurrentBiz(lead.getMthsInCurrentBiz())
                .yrsInCurrentBiz(lead.getYrsInCurrentBiz())
                .totalMthsWorkExp(lead.getTotalMthsWorkExp())
                .totalYrsWorkExp(lead.getTotalYrsWorkExp())
                .created(lead.getCreated())
                .updated(lead.getUpdated())
                .build();
    }


    public LapRequest mapLeadGenerationRequestToLapRequest(LeadGenerationRequest request){
        return LapRequest.builder()
                .FirstName(request.getFirstName())
                .LastName(request.getLastName())
                .Type__c("Individual")
                .HFCL_LOB__c(request.getProduct())
                .Organization_constitution__c(request.getConstitution())
                .PAN__c(request.getCompanyPan())
                .Pan_Individual__c(request.getPan())
                .MobilePhone(request.getMobileNumber())
                .LeadSource(request.getSource())
                .DSA_Location__c(request.getState())
                .Current_Street__c(String.valueOf(request.getPinCode()))
                .CurrentCity__c(String.valueOf(request.getPinCode()))
                .CurrentState__c(String.valueOf(request.getPinCode()))
                .Current_Country__c(String.valueOf(request.getPinCode()))
                .Current_Zip_Postal_Code__c(String.valueOf(request.getPinCode()))
                .Office_Street__c("")
                .OfficeCity__c("")
                .OfficeState__c("")
                .Office_Country__c("")
                .Office_Zip_Postal_Code__c("")
                .Bureau_Consent_Status__c("Accept")
                .Required_loan_amount__c(String.valueOf(request.getLoanAmount()))
                .Tenure_In_Months__c(String.valueOf(request.getTenure()))
                .DOB__c(String.valueOf(request.getDob()))
                .Gender__c(request.getGender())
                .RecordTypeId("0127F000000WeEKQA0")
                .OwnerId(request.getOwnerId())
                .build();
    }
}
