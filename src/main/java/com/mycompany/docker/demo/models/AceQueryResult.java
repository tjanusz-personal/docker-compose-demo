package com.mycompany.docker.demo.models;

public class AceQueryResult {

    private String totalRecords;
    private String recordCount;
    private Boolean hasMoreItems;
    private String[] resultIds;

    public String getTotalRecords() {
        return totalRecords;
    }

    public String[] getResultIds() {
        return resultIds;
    }

    public void setResultIds(String[] resultIds) {
        this.resultIds = resultIds;
    }

    public Boolean getHasMoreItems() {
        return hasMoreItems;
    }

    public void setHasMoreItems(Boolean hasMoreItems) {
        this.hasMoreItems = hasMoreItems;
    }

    public String getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(String recordCount) {
        this.recordCount = recordCount;
    }

    public void setTotalRecords(String totalRecords) {
        this.totalRecords = totalRecords;
    }

    
}
