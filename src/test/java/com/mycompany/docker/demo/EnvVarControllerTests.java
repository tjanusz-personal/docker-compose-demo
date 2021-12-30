package com.mycompany.docker.demo;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.docker.demo.keyspace.pcs.Illustration;
import com.mycompany.docker.demo.keyspace.pcs.Pcdmpg;
import com.mycompany.docker.demo.models.AnimalResponse;

import org.hamcrest.Matchers;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;



@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class EnvVarControllerTests {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private CassandraService cassandraService;

	@MockBean
	private SampleService sampleService;

	@Test
	public void getEnvHappyPath() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/env").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@Test
	public void getEnvReturnsJsonMap(CapturedOutput output) throws Exception {
		// Just make sure something comes back from the JSON to verify we have ENV
		mvc.perform(MockMvcRequestBuilders.get("/env").accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.PATH", StringContains.containsString("usr")));
	}

	@Test
	public void getSlowStuff() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/slowStuff").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().string(equalTo("slow stuff was: 14930352")));
	}

	@Test
	public void getHeartbeatHappyPath() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/heartbeat").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().string(equalTo("alive")));
	}

	@Test
	public void getInvokePublicAPIHappyPath() throws Exception {
		List<AnimalResponse> animals = new ArrayList<AnimalResponse>();
		AnimalResponse animal = new AnimalResponse();
		animal.setText("Test Text");
		animals.add(animal);
		Mockito.when(this.sampleService.invokePublicApi(Mockito.anyString())).thenReturn(animals);
		mvc.perform(MockMvcRequestBuilders.get("/invokePublicApi?amount=1").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].text", Matchers.is("Test Text")));
	}

	@Test
	public void getInvokeCassandraHappyPath() throws Exception {
		Illustration illustration = new Illustration();
		illustration.setPcd("TestPCD");
		Mockito.when(this.cassandraService.doSomethingInCassandra(null)).thenReturn(illustration);
		mvc.perform(MockMvcRequestBuilders.get("/invokeCassandra").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().json("{'pcd':'TestPCD'}"));
	}

	@Test
	public void getInvokeSolrHappyPath() throws Exception {
		List<Pcdmpg> mockedResults = new ArrayList<Pcdmpg>();
		Pcdmpg pcdmpg = new Pcdmpg();
		pcdmpg.setCdb("TEST CDB");
		pcdmpg.setMktnm("TEST MKTNM");
		mockedResults.add(pcdmpg);
		Mockito.when(this.cassandraService.doSomeSolrQuery()).thenReturn(mockedResults);
		mvc.perform(MockMvcRequestBuilders.get("/invokeSolr").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].cdb", Matchers.is("TEST CDB")))
				.andExpect(jsonPath("$[0].mktnm", Matchers.is("TEST MKTNM")));
	}	


}
