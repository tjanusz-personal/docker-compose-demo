package com.mycompany.docker.demo.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IllustrationLifeCalcRequest {

    private String product;
    private String subType;
    private String policyPages;

    public String getProduct() {
        return product;
    }

    public String getSubType() {
        return subType;
    }

    public String getPolicyPages() {
        return policyPages;
    }

    public void setProduct(String value) {
        this.product = value;
    }

    public void setSubType(String value) {
        this.subType= value;
    }
    
    public void setPolicyPages(String value) {
        this.policyPages = value;
    }

}
