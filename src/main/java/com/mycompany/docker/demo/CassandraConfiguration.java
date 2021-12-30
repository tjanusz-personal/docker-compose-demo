package com.mycompany.docker.demo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

/**
 * Configuration bean for Cassandra configuration. Since we're using spring data cassandra repository approach AND authentication had to 
 * slighly modify this to use the 'cassandraSession' approach because I couldn't figure out how to add the basic authentication
 * required! Also, this and other spring data cassandra beans all use @ConditionalOnProperty annotations so we only include
 * the Cassandra during Spring boot startup when we want to. (e.g. spring.data.cassandra.present=true)
 * Lastly, need to tell spring which repositories to scan for since they're just intefaces and won't be automatically loaded by the
 * framework on startup.
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.data.cassandra", name = "present", havingValue = "true", matchIfMissing = false)
@EnableCassandraRepositories(basePackages = "com.mycompany.docker.demo.keyspace.pcs")
public class CassandraConfiguration extends AbstractCassandraConfiguration {

    private static final Logger logger = LogManager.getLogger(CassandraConfiguration.class);

    @Autowired
    CassandraProperties cassandraProperties;

    @Override
    protected String getKeyspaceName() {
        return "pcs";
    }

    @Bean
    @Override
    public CqlSessionFactoryBean cassandraSession() {
        logger.info("Cassandra connection JSON: " + cassandraProperties.getPcs());
        // super session should be called only once
        CqlSessionFactoryBean cassandraSession = super.cassandraSession();
        cassandraSession.setUsername(cassandraProperties.userName);
        cassandraSession.setPassword(cassandraProperties.userPassword);
        cassandraSession.setContactPoints(cassandraProperties.contactPoints);
        cassandraSession.setKeyspaceName(cassandraProperties.keyspace);
        cassandraSession.setLocalDatacenter(cassandraProperties.localDataCenter);
        cassandraSession.setPort(cassandraProperties.port);
        cassandraSession.setSchemaAction(cassandraProperties.schemaAction);        
        return cassandraSession;
    }

}
