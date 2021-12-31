# Docker Compose Demo

This project contains source code and supporting files for a simple spring boot web application to be deployed onto Tomcat. 

This project attempts to demonstrate:
* Using docker containerization technology to build/containerize a spring boot application for deployment on Tomcat
* Using docker compose to deploy a spring boot application locally
* Proper environment configuration for container deployment via docker environment variables
* How to package/deploy container using AWS ECS fargate.
* How to deploy to AWS ECR repository via CLI
* Deploy image to ECR to validate AWS ECR security scanning via AWS inspector. **This project includes an older version of Log4j2 to show the CVE hit by AWS.**

Project assumptions:
* Deployment to AWS ECS (e.g. not Kubernetes based)
* Deployment to AWS Fargate (e.g. not self-managed EC2)

Additional Items to be integrated later:
* [AWS X-RAY distributed tracing/metrics](https://aws.amazon.com/xray/)

Required tools to build/run:
* Maven - [Install Maven](https://maven.apache.org/install.html)
* Docker - [Install Docker Desktop application](https://www.docker.com/)
* AWS CLI - [Install AWS CLI](https://aws.amazon.com/cli/)
* jq - [Install jq](https://stedolan.github.io/jq/manual/#Advancedfeatures)


## Project Structure

```
# Folder structure of project
├── docker-compose.yml (example docker compose file outlining deployment commands)
|
├── Dockerfile (example docker image file building)
|
|── .env_local_sample (example local file with ENV variables defined for easy local overrides)
|
├── src/
|     ├── main (Java code goes here)
|       ├── java/com/mycompany (Java code goes here)
|       ├── resources (resources go here)
|     ├── test (root directory of unit test)
|       ├── java/com/mycompany (Java code goes here)
|       ├── resources (root directory of unit test 'resource' files)
|     ├── target (deployment target directory for maven based builds)
|
├── LocalCassandra/ (Directory with instructions/samples for how to run cassandra locally)
|     ├── docker-compose-xxx.yml, etc.

```

## REST endpoints

The application contains some example endpoints to demonstrate connectivity.

| Endpoint| Example| Comments |
|--|--|--|
|env|http://localhost:8080/demo/env|return currently configured environment variables in project|
|slowStuff|http://localhost:8080/demo/slowStuff| a simple fibonacci sequence function to simulate a 'slower' function|


## Docker Information
This section outlines container specific notes for the project.

DockerFile notes:
* Assumes usage of Tomcat 9
* Assumes deployment to managed ECS (Fargate) thus no OS specifications within image definition

Docker-compose.yaml notes:
* Single service deployment
* Assumes 'latest' docker image tag from local repo
* Assume simple 8080 port mappings

## Build commands

The project assumes building a WAR file via Maven and then containerizing this file for deployment.
The docker file assumes the WAR file already exists in the standard Maven 'target' directory.


```
# Build the project locally on laptop (create WAR file in /target directory)
mvn clean package
```

```
# Build the docker image (dockerize the WAR file) and tag as 'latest'
docker build --tag docker-compose-demo-tomcat:latest .
```

## Deploy/Run the sample application (locally)

This project assumes deployment to local machine via docker compose commands. 

*Deployment to AWS ECS cluster is TBD.*

```
# deploy and start docker image as spring boot application on local machine
# This starts the container in the current window using default spring profile
docker-compose up
```

```
# OR can run using the 'local' spring profile (this overrides env properties to usable local values)
SPRING_PROFILES_ACTIVE=local docker-compose up
```

```
# OR can inject/override ENV_VARS at the command line
# this overrides the PUBLIC_API_URL_BASE value into the container thus ignoring whatever is configured in application-local.properties
PUBLIC_API_URL_BASE=http://google.com SPRING_PROFILES_ACTIVE=local docker-compose up
```

```
# OR can create a local .env file from the .env_local_sample given modified with proper values desired for local testing
# copy .env_local_sample .env
# edit the file (DO NOT CHECK THIS FILE INTO SOURCE CONTROL!)
# Do simple docker-compose up (and it will read all those env vars with all that crazy JSON strings)
docker-compose up
```

Use the browser or postman to invoke Urls to verify deployment endpoints active
* http://localhost:8080/demo/env
* http://localhost:8080/demo/slowStuff

## ENVIRONMENT VARIABLES
Since the application is to be run within a docker container all required external resource dependencies are injected via ENVIRONMENT_VARS and configured as spring properties for easy injection into various spring beans.

Notes:
* Property names in spring use . separators which map to _ with uppercase. (See examples below)
* Do NOT FORGET TO ADD THE ENVIRONMENT VARIABLES TO THE docker-compose.yml file to expose the properties to the container via injection! Failure to do so will result in you wasting hours wondering why my enviornment isn't configured correctly!
* Remember to use spring profiles to simplify your development! Local (application-local.properties) and Test (/test/resources/application.properties). If your bean depends on a property ensure to include the correct profiles to ensure proper spring startup.
* Remember can also use docker-compose up with local .env file to override various values (see previous section)

Sample properties required by the demo listed below.

| Spring property | ENV VARIABLE| Comments |
|--|--|--|
|secret.from.secrets.mgr|SECRET_FROM_SECRETS_MGR|sample secret being inject to env (sample only)|
|aws.xray.sdk.enabled|AWS_XRAY_SDK_ENABLED|enable AWS X-ray integration|
|ace.api.url.base|ACE_API_URL_BASE|injected base url for the ACE Submission API URL.|
|ace.api.basic.auth|ACE_API_BASIC_AUTH|injected secrets string for the Authorization header security requirement|


## Unit tests

Tests for the functions are defined in the `/src/test` folder in this project. These tests utilize spring boot test features.

```bash
# invoke normal test target to run all unit tests
mvn test
```

## Integration tests

There are no integration tests.

## Logging Integration
The POC integrated Log4j2 as the default logger implementation. Both sample endpoints contain simple logging code to generate simple log statements (INFO, DEBUG).

Additional Notes:
* All logging is configured to a single Console based appender (not file based appender). This was done since it is assumed the AWS ECS TaskDefinition utilizes the 'awslogs' driver to send all logs to CloudWatch.
* All non-test loggging utilizes the JSON format. This format configured based on [the following properties](http://logging.apache.org/log4j/2.x/manual/layouts.html#JSONLayout). Further investigation is required to utilize the existing Penn Mutual JSON logging format is consistent with other Java based applications.
* The test/resources directory contains a separate log4j2-test.yml file with a NON JSON configured appender. This separate appender was added to simplify local unit testing error triage (Text easier to read locally than JSON)
* Further investigation is required to ensure ALL spring related logging is configured for JSON format! The POC limited testing to application specific messages only.
* The spring 'local' profile is configured to use the log4j2-local.yml file for local non JSON logging.

## Secrets Manager Integration
The POC integrated secrets assuming ECS injects the values via environment variables.

NOTE: the values of secrets are typically returned as JSON strings!
```
# secret value (TestSecret = LocalTestValue)
SECRET_FROM_SECRETS_MGR = "{\"TestSecret\":\"LocalTestValue\"}"
```

There is a ENV variable (SECRET_FROM_SECRETS_MGR) which is mapped to a spring property (secret.from.secrets.mgr).

When running ECS mode the task deployment automatically populates this value.  Running in local mode requires the developer to override the spring property in the application-local.properties file.

These values can easily be seen using the spring actuator endpoint
http://localhost:8080/demo/actuator/env (this dumps all properties/environment variables)

## Public API Integration
The POC integrated a sample endpoint (/invokeAceApi) which invokes a public REST call to a public internet service.
https://cat-fact.herokuapp.com/facts/random?animal_type=dog&amount=2

Notes:
* The base URL is injected via environment variable and mapped to a spring property thus permitting configuration across all environments
* The default application.properties file does NOT include a default value and it is assumed this will be configured during deployment
* The local profile uses a hard coded value for easy local testing
* Spring Boot WebClient is utilized as the REST Client mechanism. This uses a reactive programming model with Flux/Mono results. This was utilized since spring is deprecating RestTemplate.
* Unit testing utilizes the MockWebServer (https://github.com/square/okhttp/tree/master/mockwebserver) since this is Spring's recommended testing approach

| Spring property | ENV VARIABLE| Comments |
|--|--|--|
|public.api.url.base|PUBLIC_API_URL_BASE|injected base url for the cats/dogs URL.|


## Cassandra Integration
The POC integrated a sample endpoint (/demo/invokeCassandra) which connects to the DEV cassandra cluster on Prem using the PCS keyspace. The main controller utilizes a simple spring service bean "CassandraService" to perform a basic hard wired select query from a sample PCS Illustrations repository.


Notes:
* Spring Data Cassandra module with Repository support (e.g. auto proxies for basic CRUD methods). Using this approach resulted in less DAO code but requires an active/valid autenticated connection during spring boot startup! Enabling connection to cassandra can be toggled on/off using the **SPRING_DATA_CASSANDRA_PRESENT=true** environment variable. If Cassandra enabled you MUST provide a valid secret string **CASSANDRA_PCS** or spring won't boot! This is disabled by default and all associated spring cassandra beans have been autowired lazily to prevent Spring Boot from always trying to wire up Cassandra.
* All Cassandra configuration properties are assumed to be stored in AWS Secrets Manager as a JSON string of Keys/Value. Application startup will search for this JSON string in **CASSANDRA_PCS** and parse the JSON accordingly into various required Cassandra values (e.g. connection points, user name, password, etc.). Refer to the CassandraProperties class for more information on how this works.
* Cassandra configuration is managed by the CassandraConfiguration bean. Refer to that class for more information.
* There is a single CassandraService integration test but it is currently disabled due to desire to NOT have the POC code be always be wiring to DEV Cassandra clusters. Unit/Integration testing using local cassandra was out of the scope of this POC.

## Solr Query Integration
The POC integrated a sample endpoint (/demo/invokeSolr) which performs a solr query to the DEV cassandra cluster on Prem using the PCS keyspace. 


The solr integration utilizes the same cassandra configuration (refer to Cassandra Integration section) so all existing restrictions/notes apply to this section as well. 

Notes:
* There is a spring data solr project but it is marked for deprecation (https://spring.io/projects/spring-data-solr). As a result, the POC code relied upon the existing cassandra configuration/connections to perform sample solr query using the solr_query column.
* A "PcdmgpRepository" was configured with an additional 'List<Pcdmpg> findBySolrQuery(String solrQuery);' repository method to perform the query.

## Local Cassandra Integration
The POC also utilized datastax docker images to download/execute local versions of DSE and dataStax studio. Refer to the LocalCassandra directory README.md file for more information.


## AWS X-RAY Integration
The POC integrated the sample AWS X-RAY JDK to understand limits/features of the service.

The POC only enabled metric collection locally and did NOT yet test full integration in ECS/Fargate!

The Java X-RAY SDK has numerous limitations which required spring workarounds.

Additional Notes:
* The current version assumes spring boot apps are using JPA (for some reason) so these files were included in the POM.XML but the underlying JPA spring boot classes were 'excluded' from startup. Refer to [this link here for the issue](https://github.com/aws/aws-xray-sdk-java/issues/45)
* The current version does NOT have a simple enable/disable environment variable to enable/disable metric gathering/sending (https://github.com/aws/aws-xray-sdk-java/issues/178).  The POC was able to utilize spring's @ConditionalOnProperty with a combination of env vars to work around this limitiation. 
* The POC configuration assumes xray is disabled by default. It is assumed during deployment this 'switch' can be enabled using environment variables in the docker-compose files (see below).
* The POC configured X-Ray to capture from the HTTP request to the controller bean. (XRayInspector, AwsXRayConfiguration)

```
# Enable XRAY tracing locally (need to ensure have local agent installed correctly)
AWS_XRAY_SDK_ENABLED=true docker-compose up
```

## Docker packaging/tagging
This section outlines some commands for building/tagging the docker images and pushing to ECR repository

[AWS ECR repository cli commands](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/ecr/index.html)

```
# create the AWS ECR repository (only need to do this one time)
aws ecr create-repository --repository-name docker-compose-demo-tomcat --image-scanning-configuration scanOnPush=true --image-tag-mutability MUTABLE
## returns an ARN like this..
arn:aws:ecr:us-east-1:111111111:repository/docker-compose-demo-tomcat
## the returned ARN prefix can be used for tagging/ecr pushes (see below)

## List out all current repos
aws ecr describe-repositories

# list images in repo
aws ecr list-images --repository-name docker-compose-demo-tomcat

# describe all images w/in a repo
aws ecr describe-images --repository-name docker-compose-demo-tomcat

```

[Use docker commands to build/tag the image before pushing to AWS ECR repo](https://docs.docker.com/engine/reference/commandline/cli/)

```
# Build the docker image (dockerize the WAR file) 
docker build -t docker-compose-demo-tomcat .

-- tag current version to specific value
docker tag docker-compose-demo-tomcat:latest 111111111.dkr.ecr.us-east-1.amazonaws.com/docker-compose-demo-tomcat:version1

-- tag version to be latest
docker tag docker-compose-demo-tomcat:latest 111111111.dkr.ecr.us-east-1.amazonaws.com/docker-compose-demo-tomcat:latest

-- push that tag to repo
docker push 111111111.dkr.ecr.us-east-1.amazonaws.com/docker-compose-demo-tomcat:latest

-- tag image with next value (version2)
docker tag docker-compose-demo-tomcat:latest 111111111.dkr.ecr.us-east-1.amazonaws.com/docker-compose-demo-tomcat:version2

-- remove image tag (version1)
docker rmi 111111111.dkr.ecr.us-east-1.amazonaws.com/docker-compose-demo-tomcat:version1

```

## AWS Inspector findings
W/in my AWS account I have enabled AWS inspector to automatically scan all ECR based images push to my ECR repos.

On push of container image it should start an auto scan.. First glance seems like we need to ensure we have containers that are built according to AWS 'standards'.
For example, if I tried to just use a simple 'tomcat:9' container AWS scan results returned 'no findings' but using 'tomcat:9-jdk17-corretto' resulted in actual scan findings.

```
# see finding for specific image tag..
aws ecr describe-image-scan-findings --repository-name docker-compose-demo-tomcat --image-id imageDigest=sha256:get_sha_from_image
```

These are some various [aws cli commands to interact with inspector2](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/inspector2/index.html)

```
# show all covered resouce ids
aws inspector2 list-coverage | jq '.coveredResources[].resourceId'

# select out a subset of findings into csv format
aws inspector2 list-findings | jq '.findings[] | [.awsAccountId, .inspectorScore, .severity, .type, .title] | @csv'

# pull out all resources with id and type
aws inspector2 list-findings | jq '.findings[].resources[] | [.id, .type] | @csv'

# pull out key elements to see list of main issues
aws inspector2 list-findings | jq '.findings[] | {accountId:.awsAccountId, type:.type, severity:.severity, title:.title, score:.inspectorScore, res_type:.resources[].type, res_id:.resources[].id }'

# alpha sort findings by severity
aws inspector2 list-findings | jq '.findings | sort_by(.severity)[] | {accountId:.awsAccountId, type:.type, severity:.severity, title:.title, score:.inspectorScore, res_type:.resources[].type, res_id:.resources[].id }'

# alpha sort findings by severity and return as csv file
aws inspector2 list-findings | jq '.findings | sort_by(.severity)[] | [.awsAccountId, .type, .severity, .title, .inspectorScore, .resources[].type, .resources[].id ]  | @csv'

```

Refer to the security_documentation README.MD for more infomation.


## TODO Items
* AWS X-RAY integration through to fargate


## Additional Resources

* [Spring Boot](https://spring.io/projects/spring-boot)
* [AWS Fargate](https://aws.amazon.com/fargate/)