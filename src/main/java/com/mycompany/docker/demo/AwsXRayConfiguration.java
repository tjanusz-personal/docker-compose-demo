package com.mycompany.docker.demo;

import javax.servlet.Filter;

import com.amazonaws.xray.javax.servlet.AWSXRayServletFilter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration bean for injecting the AWS X-RAY servlet filter for all HTTP requests.
 * This bean should only be wired when the environment variable AWS_XRAY_SDK_ENABLED=true exists.
 * This way we can run locally without metrics (local container, tests, etc.)
 */
@Configuration
@ConditionalOnProperty(prefix = "aws.xray.sdk", name = "enabled", havingValue = "true", matchIfMissing = false)
public class AwsXRayConfiguration {

    @Bean
    public Filter TracingFilter() {
        // This is default enable tracing across all requests
        return new AWSXRayServletFilter("docker-compose-demo");
        
        // FYI - if Local/Unit test can configure to use NoSamplingStrategy (do nothing)
        // Keeping this example code for demonstration purposes
        // AWSXRayRecorderBuilder builder = AWSXRayRecorderBuilder.standard();
        // builder.withSamplingStrategy(new NoSamplingStrategy());
        // AWSXRayRecorder recorder = builder.build();
        // return new AWSXRayServletFilter(new FixedSegmentNamingStrategy("docker-compose-demo"), recorder);
    }
}