package com.example.cqrs.retry.config;

import com.example.cqrs.retry.ExceptionInterceptor;
import com.example.cqrs.retry.RetrySettings;
import com.example.cqrs.retry.SimpleRetryException;
import lombok.AllArgsConstructor;
import org.springframework.classify.SubclassClassifier;
import org.springframework.cloud.stream.annotation.StreamRetryTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor
@EnableRetry
public class RetryTemplateConfigs {

    private RetrySettings retrySettings;

    @StreamRetryTemplate
    public RetryTemplate streamRetryTemplate(ExceptionInterceptor exceptionInterceptor) throws ClassNotFoundException {

        SubclassClassifier exceptionClassHierarchy = new SubclassClassifier();
        exceptionClassHierarchy.setTypeMap(createExceptionPolicyRules());

        ExceptionClassifierRetryPolicy exceptionclassifierRetryPolicy = new ExceptionClassifierRetryPolicy();
        exceptionclassifierRetryPolicy.setExceptionClassifier(exceptionClassHierarchy);

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(exceptionclassifierRetryPolicy);
        retryTemplate.registerListener(exceptionInterceptor);
        return retryTemplate;
    }

    private Map<Class<? extends Throwable>, RetryPolicy> createExceptionPolicyRules() throws ClassNotFoundException {

        //Default Exception Policies creation
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(1);
        Map<Class<? extends Throwable>, RetryPolicy> retryPolicyMap = new HashMap<>() {{
            put(SimpleRetryException.class, simpleRetryPolicy);
        }};

        //User Defined Exception Policies creation
        if (retrySettings.getSimpleRetryExceptions() != null) {
            for (String exception : retrySettings.getSimpleRetryExceptions()) {
                Class<? extends Throwable> clazz = (Class<? extends Throwable>) Class.forName(exception);
                retryPolicyMap.put(clazz, simpleRetryPolicy);
                return retryPolicyMap;
            }
        }
        return retryPolicyMap;
    }
}
