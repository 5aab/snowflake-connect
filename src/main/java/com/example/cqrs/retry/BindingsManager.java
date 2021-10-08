package com.example.cqrs.retry;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.binder.Binding;
import org.springframework.cloud.stream.binding.BindingsLifecycleController;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class BindingsManager {

    private BindingsLifecycleController bindingsController;

    //https://docs.spring.io/spring-cloud-stream/docs/3.1.3/reference/html/spring-cloud-stream.html#binding_visualization_control
    public void stopConsumption(String bindingName) {
        Binding binding = bindingsController.queryState("echo-in-0");
        //assertThat(binding.isRunning()).isTrue();
        bindingsController.changeState("echo-in-0", BindingsLifecycleController.State.STOPPED);
        //Alternative way of changing state. For convenience we expose start/stop and pause/resume operations.
        //bindingsController.stop("echo-in-0")
      //  assertThat(binding.isRunning()).isFalse();
        log.info("Message consumption paused for {}", bindingName);
    }

    public void startConsumption(String bindingName) {
        //   bindingsEndpoint.changeState(bindingName, BindingsLifecycleController.State.STARTED);
        log.info("Message consumption started for {}", bindingName);
    }

}
