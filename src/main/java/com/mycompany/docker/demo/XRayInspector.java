package com.mycompany.docker.demo;

import java.util.Map;

import com.amazonaws.xray.entities.Subsegment;
import com.amazonaws.xray.spring.aop.AbstractXRayInterceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Spring AOP Interceptor for injecting the AWS X-RAY metrics to the running application.
 * This aspect should only be wired when the environment variable
 * AWS_XRAY_SDK_ENABLED=true exists. This way we can run locally without metrics
 * (local container, tests, etc.).
 * Also note this pointcut configured for all 'bean' methods so not sure what we'd really want here.
 */
@Aspect
@Component
@ConditionalOnProperty(prefix = "aws.xray.sdk", name = "enabled", havingValue = "true", matchIfMissing = false)
public class XRayInspector extends AbstractXRayInterceptor {
    @Override
    protected Map<String, Map<String, Object>> generateMetadata(ProceedingJoinPoint proceedingJoinPoint,
            Subsegment subsegment) {
        return super.generateMetadata(proceedingJoinPoint, subsegment);
    }

    @Override
    @Pointcut("@within(com.amazonaws.xray.spring.aop.XRayEnabled) && bean(*)")
    public void xrayEnabledClasses() {
    }

}