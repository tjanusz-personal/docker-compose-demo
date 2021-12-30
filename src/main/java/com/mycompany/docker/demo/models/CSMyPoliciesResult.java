package com.mycompany.docker.demo.models;

public class CSMyPoliciesResult {

    private CSPolicy[] policies;
    private String count;
    private String lifePolicies;
    private String numFound;

    public CSPolicy[] getPolicies() {
        return policies;
    }

    public String getNumFound() {
        return numFound;
    }

    public void setNumFound(String numFound) {
        this.numFound = numFound;
    }

    public String getLifePolicies() {
        return lifePolicies;
    }

    public void setLifePolicies(String lifePolicies) {
        this.lifePolicies = lifePolicies;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setPolicies(CSPolicy[] policies) {
        this.policies = policies;
    }
    
}
