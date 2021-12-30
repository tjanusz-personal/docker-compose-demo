package com.mycompany.docker.demo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.mycompany.docker.demo.keyspace.pcs.Illustration;
import com.mycompany.docker.demo.keyspace.pcs.IllustrationRepository;
import com.mycompany.docker.demo.keyspace.pcs.Pcdmpg;
import com.mycompany.docker.demo.keyspace.pcs.PcdmpgRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(prefix = "spring.data.cassandra", name = "present", havingValue = "true", matchIfMissing = false)
@Service
public class CassandraService {

    private static final Logger logger = LogManager.getLogger(CassandraService.class);

    @Autowired
    private IllustrationRepository illustrationRepository;

    @Autowired
    private PcdmpgRepository pcdmpgRepository;

    public Illustration doSomethingInCassandra(String uiidValue) {
        UUID uid= null;
        if (uiidValue == null) {
            uid = UUID.fromString("7ef202d1-f26b-46c2-95ca-b57e548bcd6a"); 
        } else {
            uid = UUID.fromString(uiidValue);
        }
        logger.info("Going to start Cassandra query");
        Optional<Illustration> findById = illustrationRepository.findById(uid);
        Illustration illustration = findById.get();
        return illustration;
    }

    public List<Pcdmpg> doSomeSolrQuery() {
        logger.info("Going to start Solr query");
        String solrQuery = "cdb:TC9";
        List<Pcdmpg> findBySolr_query = pcdmpgRepository.findBySolrQuery(solrQuery);
        System.out.println("#### query result size is: " + findBySolr_query.size());
        System.out.println(findBySolr_query.get(0).prettyPrint());
        return findBySolr_query;
    }


}