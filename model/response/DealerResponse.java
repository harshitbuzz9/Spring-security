package com.bridge.herofincorp.model.response;

import com.bridge.herofincorp.model.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor
public class DealerResponse {
    private String username;
    private String dealerCode;
    private String sapVendorcode;
    private String dealerName;
    private String pan;
    private String gst;
    private String gstClass;
    private String eInvoice;
    private String vendorStateCode;
    private String vendorCity;
    private String vendorDistrict;
    private String vendorAddress;
    private String vendorAddress1;
    private String vendorCountry;
    private String vendorPostalCode;
    private String companyCode;
    private String accountGroup;
    private String status;
    private String emailId;
    private String mobileNo;
    private String vendorState;
    private String vendorCreationDate;
    private List<ProductDto> products;

}
