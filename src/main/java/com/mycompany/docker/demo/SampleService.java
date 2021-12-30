package com.mycompany.docker.demo;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.docker.demo.models.AnimalResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class SampleService {

    private static final Logger logger = LogManager.getLogger(SampleService.class);

    @Value("${public.api.url.base}")
    private String publicApiUrlBase;

    @Value("${ace.api.url.base}")
    private String aceApiUrlBase;

    @Value("${ace.api.basic.auth}")
    private String aceApiBasicAuthJson;

    @Value("${coreservices.api.url.base}")
    private String coreServicesApiUrlBase;

    @Value("${illustrations.calc.api.url.base}")
    private String illustrationsCalcApiUrlBase;

    @Value("${illustrations.calc.api.url.header}")
    private String illustrationsCalcApiUrlHeader;    

    @Value("${ecs.api.url.base}")
    private String ecsApiUrlBase;

    public void setPublicApiUrlBase(String urlBase) {
        this.publicApiUrlBase = urlBase;
    }

    public void setAceApiUrlBase(String aceUrlBase) {
        this.aceApiUrlBase = aceUrlBase;
    }

    public void setAceApiBasicAuthJson(String authJson) {
        this.aceApiBasicAuthJson = authJson;
    }

    public void setCoreservicesApiUrlBase(String urlBase) {
        this.coreServicesApiUrlBase = urlBase;
    }

    public void setIllustrationsCalcApiUrlBase(String urlBase) {
        this.illustrationsCalcApiUrlBase = urlBase;
    }

    public void setIllustrationsCalcApiUrlHeader(String urlHeader) {
        this.illustrationsCalcApiUrlHeader = urlHeader;
    }

    public void setEcsApiUrlBase(String urlBase) {
        this.ecsApiUrlBase = urlBase;
    }    

    public List<AnimalResponse> invokePublicApi(String amountString) {
        logger.info("SampleService: invokePublicApi - Begin");

        logger.info("##### base url is:" + this.publicApiUrlBase);

        // Invoke simple Heroku API call for cats/dogs
        // https://cat-fact.herokuapp.com/facts/random?animal_type=dog&amount=2
        WebClient client3 = WebClient.builder().baseUrl(publicApiUrlBase)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
        List<AnimalResponse> animals = client3.get()
                .uri(uriBuilder -> uriBuilder.path("facts/random").queryParam("animal_type", "cat")
                        .queryParam("amount", amountString).build())
                .retrieve().bodyToFlux(AnimalResponse.class).collectList().block();

        logger.info("SampleService: invokePublicApi - end");
        return animals;
    }

    public Map<String, String> getInvokeECSEnv() {
        // method will make HTTP call to another instance of this application running in
        // another cluster

        logger.info("SampleService: getInvokeECSEnv - Begin");

        // Sample Full URL
        // http://internal-sandbox-docker-echo-poc-alb-1573120749.us-east-1.elb.amazonaws.com/demo/env
        String baseUrl = ecsApiUrlBase;
        logger.info("##### ecs base url is:" + ecsApiUrlBase);

        // Invoke URL with no headers
        WebClient client3 = WebClient.builder().baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();

         Map<String, String> ecsResult = client3.get()
                .uri(uriBuilder -> uriBuilder.path("demo/env").build()).retrieve()
                .bodyToMono(Map.class).block();

        logger.info("SampleService: getInvokeECSEnv - End");
        return ecsResult;
    }

    String getSecretValueFromJsonString(String jsonSecretString, String keyName) {
        // "{\"ACE_API_AUTH_TOKEN\":\"Basic XXXXXXXXXX=\"}"
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> map = mapper.readValue(jsonSecretString, Map.class);
            return (String) map.get(keyName);
        } catch (Exception ex) {
            return "unableToParseSecret";
        }
    }

    void dummyMethodToDemonstrateUpdate() {
        // do nothing here..
    }

}