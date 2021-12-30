package com.mycompany.docker.demo.keyspace.pcs;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Sample PCS Illustration table mapping. I only selected this one due to it have 3 fields!
 */
@Table("Illustration")
public class Illustration {
    
    @Id
    @PrimaryKeyColumn(name = "id", type= PrimaryKeyType.PARTITIONED)
    private UUID id;

    @Column
    private String pcd;

    @Column
    private Boolean run;

    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }    

    public Boolean getRun() {
        return run;
    }

    public String getPcd() {
        return pcd;
    }

    public void setRun(Boolean run) {
        this.run = run;
    }

    public void setPcd(String pcd) {
        this.pcd = pcd;
    }

}
