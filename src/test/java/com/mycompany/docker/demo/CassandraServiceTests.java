package com.mycompany.docker.demo;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CassandraServiceTests {

	@Autowired(required = false)
	private CassandraService cassandraService;


	@Disabled("don't want to always run connected to cassandra in DEV. This is only here to help test things locally")
	@Test
	public void doCassandraStuff() throws JsonProcessingException, InterruptedException {
		System.out.println("**** gonna call service");
		// String uiidString = "cdd3d624-e83d-4d02-a909-3572c2c13925";
		cassandraService.doSomethingInCassandra(null);
		System.out.println("**** done call service");
	}

	@Disabled("don't want to always run connected to cassandra in DEV. This is only here to help test things locally")
	@Test
	public void doSolrStuff() throws JsonProcessingException, InterruptedException {
		System.out.println("**** gonna call service");
		cassandraService.doSomeSolrQuery();
		System.out.println("**** done call service");
	}

}
