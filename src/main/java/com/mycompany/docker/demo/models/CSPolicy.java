package com.mycompany.docker.demo.models;

public class CSPolicy {

    private String polNumber;
    private String source;
    private String lineOfBusiness;

    public String getPolNumber() {
        return polNumber;
    }

    public String getLineOfBusiness() {
        return lineOfBusiness;
    }

    public void setLineOfBusiness(String lineOfBusiness) {
        this.lineOfBusiness = lineOfBusiness;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setPolNumber(String polNumber) {
        this.polNumber = polNumber;
    }

    
}
