package com.mycompany.docker.demo.keyspace.pcs;

import java.util.List;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

/**
 */
@ConditionalOnProperty(prefix = "spring.data.cassandra", name = "present", havingValue = "true", matchIfMissing = false)
@Repository
public interface PcdmpgRepository extends CassandraRepository<Pcdmpg, UUID> {
    
    List<Pcdmpg> findBySolrQuery(String solrQuery);
}
