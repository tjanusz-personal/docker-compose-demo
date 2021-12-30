package com.mycompany.docker.demo.keyspace.pcs;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Sample PCS pcdmpg table mapping. I only selected this one due to it have 3 fields!
 */
@Table("pcdmpg")
public class Pcdmpg {
    
    @Id
    @PrimaryKeyColumn(name = "id", type= PrimaryKeyType.PARTITIONED)
    private UUID id;

    @Column
    private String cdb;

    @Column
    private String mktnm;

    @Column
    private String pcds;
    
    @Column
    private String ptyp;

    @Column
    private String ptyptc;

    @Column("solr_query")
    private String solrQuery;
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }

    public String getCdb() {
        return cdb;
    }

    public String getMktnm() {
        return mktnm;
    }
    
    public String getPcds() {
        return pcds;
    }

    public String getPtyp() {
        return ptyp;
    }

    public String getPtyptc() {
        return ptyptc;
    }

    public String getSolrQuery() {
        return solrQuery;
    }    

    public void setCdb(String aValue) {
        cdb = aValue;
    }

    public void setMktnm(String aValue) {
        mktnm = aValue;
    }

    public void setPcds(String aValue) {
        pcds = aValue;
    }

    public void setPtyp(String aValue) {
        ptyp = aValue;
    }

    public void setPtyptc(String aValue) {
        ptyptc = aValue;
    }
    
    public void setSolrQuery(String aValue) {
        solrQuery = aValue;
    }    

    public String prettyPrint() {
        StringBuilder builder = new StringBuilder();
        builder.append("id: ");
        builder.append(this.id);
        builder.append(" cdb: ");
        builder.append(this.cdb);
        builder.append(" mktnm:");
        builder.append(this.mktnm);
        builder.append(" pcds:");
        builder.append(this.pcds);
        builder.append(" ptyp:");
        builder.append(this.ptyp);
        builder.append(" ptyptc:");
        builder.append(this.ptyptc);        
        return builder.toString();
    }

}
