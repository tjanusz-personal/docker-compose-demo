package com.mycompany.docker.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.docker.demo.models.AnimalResponse;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@SpringBootTest
class SampleServiceTests {

	@Autowired
	private SampleService sampleService;
	private String mockBaseUrl;

	public static MockWebServer mockBackEnd;
	private ObjectMapper MAPPER = new ObjectMapper();

	@BeforeAll
	static void setUp() throws IOException {
		mockBackEnd = new MockWebServer();
		mockBackEnd.start();
	}

	@AfterAll
	static void tearDown() throws IOException {
		mockBackEnd.shutdown();
	}

	@BeforeEach
	void initialize() throws IOException {
		mockBaseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
	}

	@Test
	public void invokePublicApiHappyPath() throws JsonProcessingException, InterruptedException {
		sampleService.setPublicApiUrlBase(mockBaseUrl);
		List<AnimalResponse> mockResponses = new ArrayList<AnimalResponse>();
		AnimalResponse response = new AnimalResponse();
		response.setDeleted(Boolean.FALSE);
		response.setText("Unit Test Response");
		response.setType("Cat");
		mockResponses.add(response);
		mockBackEnd.enqueue(new MockResponse().setBody(MAPPER.writeValueAsString(mockResponses))
				.addHeader("Content-Type", "application/json"));

		// invoke service
		List<AnimalResponse> actualResponse = sampleService.invokePublicApi("3");

		// assert response was what we wanted
		assertEquals(1, actualResponse.size());
		assertEquals("Unit Test Response", actualResponse.get(0).getText());
		assertEquals("Cat", actualResponse.get(0).getType());
		assertEquals(Boolean.FALSE, actualResponse.get(0).getDeleted());

		// ensure the recorded requests seemed right too
		RecordedRequest recordedRequest = mockBackEnd.takeRequest();
		assertEquals("GET", recordedRequest.getMethod());
		assertEquals("/facts/random?animal_type=cat&amount=3", recordedRequest.getPath());
	}

	@Test
	public void invokePublicApiThrowsExceptionsOnHTTPErrors() throws JsonProcessingException, InterruptedException {
		sampleService.setPublicApiUrlBase(mockBaseUrl);
		mockBackEnd.enqueue(new MockResponse().setResponseCode(404));

		WebClientException exception = assertThrows(WebClientException.class, () -> {
			sampleService.invokePublicApi("3");
		});
		// make call to pop the request off the 'enqueue' (stinks but if don't do this will see incorrect later takeRequest() calls)
		mockBackEnd.takeRequest();

		assertTrue(exception.getMostSpecificCause().getMessage().contains("404 Not Found"));
	}

	@Test
	public void getSecretValueFromJsonStringReturnsValueFromKeyInJson() {
		String sampleSecretJson = "{\"ACE_API_AUTH_TOKEN\":\"Basic lrfesfsdf=\"}";
		String secretValue = sampleService.getSecretValueFromJsonString(sampleSecretJson, "ACE_API_AUTH_TOKEN");
		assertEquals("Basic lrfesfsdf=", secretValue);
	}

	@Test
	public void getSecretValueFromJsonStringSwallowsExceptionAndReturnsErrorString() {
		String sampleSecretJson = "invalid json string";
		String secretValue = sampleService.getSecretValueFromJsonString(sampleSecretJson, "ACE_API_AUTH_TOKEN");
		assertEquals("unableToParseSecret", secretValue);
	}

	@Test
	public void getSecretValueFromJsonStringWithInvalidKeyReturnsNull() {
		String sampleSecretJson = "{\"ACE_API_AUTH_TOKEN\":\"Basic lrfesfsdf=\"}";
		String secretValue = sampleService.getSecretValueFromJsonString(sampleSecretJson, "INVALID_KEY");
		assertNull(secretValue);
	}

	@Test
	public void getInvokeECSEnvHappyPath()
			throws JsonProcessingException, InterruptedException, SSLException {
		sampleService.setEcsApiUrlBase(mockBaseUrl);
		Map<String, String> response = new HashMap<String,String>();
		response.put("ENV_1", "ENV_1_VALUE");
		mockBackEnd.enqueue(new MockResponse().setBody(MAPPER.writeValueAsString(response)).addHeader("Content-Type",
				"application/json"));

		// invoke service
		Map<String, String> actualResponse = sampleService.getInvokeECSEnv();

		// assert response was what we wanted
		assertEquals(1, actualResponse.size());
		assertEquals("ENV_1_VALUE", actualResponse.get("ENV_1"));

		// ensure the recorded requests seemed right too
		RecordedRequest recordedRequest = mockBackEnd.takeRequest();
		assertEquals("GET", recordedRequest.getMethod());
		assertEquals("/demo/env", recordedRequest.getPath());
		assertEquals(MediaType.APPLICATION_JSON_VALUE, recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE));
	}

}
