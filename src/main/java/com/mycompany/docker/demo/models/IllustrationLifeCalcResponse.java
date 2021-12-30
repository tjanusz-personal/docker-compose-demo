package com.mycompany.docker.demo.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IllustrationLifeCalcResponse {

    private String return_scope;
    private String return_ssoToken;
    private String return_product;
    private String return_subType;
    private String return_policyPages;
    private String return_greeting;

    public String getReturn_scope() {
        return return_scope;
    }

    public String getReturn_ssoToken() {
        return return_ssoToken;
    }

    public String getReturn_product() {
        return return_product;
    }

    public String getReturn_subType() {
        return return_subType;
    }
    
    public String getReturn_policyPages() {
        return return_policyPages;
    }
    
    public String getReturn_greeting() {
        return return_greeting;
    }

    public void setReturn_scope(String value) {
        this.return_scope = value;
    }

    public void setReturn_ssoToken(String value) {
        this.return_ssoToken = value;
    }
    
    public void setReturn_product(String value) {
        this.return_product = value;
    }

    public void setReturn_subType(String value) {
        this.return_subType = value;
    }

    public void setReturn_policyPages(String value) {
        this.return_policyPages = value;
    }

    public void setReturn_greeting(String value) {
        this.return_greeting = value;
    }
    
    
}
