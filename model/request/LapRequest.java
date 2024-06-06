package com.bridge.herofincorp.model.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LapRequest {
    private String FirstName;
    private String LastName;
    private String Type__c;
    private String HFCL_LOB__c;
    private String Organization_constitution__c;
    private String PAN__c;
    private String Pan_Individual__c;
    private String MobilePhone;
    private String LeadSource;
    private String DSA_Location__c;
    private String Current_Street__c;
    private String CurrentCity__c;
    private String CurrentState__c;
    private String Current_Country__c;
    private String Current_Zip_Postal_Code__c;
    private String Office_Street__c;
    private String OfficeCity__c;
    private String OfficeState__c;
    private String Office_Country__c;
    private String Office_Zip_Postal_Code__c;
    private String Bureau_Consent_Status__c;
    private String Required_loan_amount__c;
    private String Tenure_In_Months__c;
    private String DOB__c;
    private String Gender__c;
    private String RecordTypeId;
    private String OwnerId;
}
