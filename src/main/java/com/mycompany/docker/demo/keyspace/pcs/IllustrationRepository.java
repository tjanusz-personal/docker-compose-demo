package com.mycompany.docker.demo.keyspace.pcs;

import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

/**
 * Sample Illustration repository interface for basic CRUD functions. NOTE: we've only tested using the simple findById method.
 * This interface also follows @ConditionalOnProperty to ensure its only loaded when we think we want to load Cassandra. Failing to 
 * use this toggle properly will result in spring startup errors stating things like "must create IllustrationRepository bean". If you 
 * see an error like that it means that something is incorrectly wired up!
 */
@ConditionalOnProperty(prefix = "spring.data.cassandra", name = "present", havingValue = "true", matchIfMissing = false)
@Repository
public interface IllustrationRepository extends CassandraRepository<Illustration, UUID> {
    
}
