package com.mycompany.docker.demo.models;

public class AnimalResponse {
    private String type;
    private Boolean deleted;
    private String text;

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public void setType(String type) {
        this.type = type;
    }

    
}
