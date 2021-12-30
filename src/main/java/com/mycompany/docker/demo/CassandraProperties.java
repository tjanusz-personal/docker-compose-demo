package com.mycompany.docker.demo;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.SchemaAction;

/**
 * Spring Configuration Properties bean to provide a simple interface for all
 * the required Cassandra properties for configuration. This bean accepts a
 * single JSON string (assume coming from an AWS SecretsManager string) and
 * automatically parses the JSON to the various properties required by Spring
 * Data Cassandra. We could investigate using something like Spring Cloud AWS
 * for this but that seemed heavyweight for a simple POC like this. 
 * Required JSON String fields:
 * "USER_NAME":"XXXXXXX",
 * "USER_PASSWORD":"XXXXXXXX",
 * "CONTACT_POINTS":"ddse101.mycompany.com,ddse102.mycompany.com,ddse103.mycompany.com,ddse104.mycompany.com"
 */
@Configuration
@ConfigurationProperties(prefix = "cassandra")
public class CassandraProperties {

    private static final Logger logger = LogManager.getLogger(CassandraProperties.class);
    
    public String pcs;
    public int port;
    public String userName;
    public String userPassword;
    public String localDataCenter;
    public String contactPoints;
    public String keyspace;
    public SchemaAction schemaAction;

    public String getPcs() {
        return pcs;
    }

    /**
     * This is the setter that the spring framework will invoke when it finds the property
     */
    public void setPcs(String someJsonString) throws JsonMappingException, JsonProcessingException {
        this.pcs = someJsonString;
        // super hacky trick since spring will auto inject this via setter 
        // intercept it and fill in rest of properties.
        logger.info("#### Going to parse out JSON AWS Secret String! " + someJsonString);
        convertSecretToProperties(someJsonString);
    }

    /**
     * take the JSON string and parse it apart into the the other properties we need
     * this way I don't have to use Spring Cloud AWS stuff since all I want is a
     * simple JSON to properties converter function.
     */
    void convertSecretToProperties(String jsonSecretString) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(jsonSecretString, Map.class);
        this.userName = (String) map.get("USER_NAME");
        this.userPassword = (String) map.get("USER_PASSWORD");
        this.contactPoints = (String) map.get("CONTACT_POINTS");
        // default these values for now
        this.port = 9042;
        this.localDataCenter="Search";
        this.keyspace="pcs";
        this.schemaAction=SchemaAction.NONE;
    }

}