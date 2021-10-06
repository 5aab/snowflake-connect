package com.example.cqrs.event.config;

import com.example.cqrs.event.relay.MessageRelayHandler;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.router.HeaderValueRouter;
import org.springframework.messaging.MessageChannel;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class RelayConfig {

    @Bean(name = "relayMessageRouterChannel")
    public MessageChannel relayMessageRouterChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "relayMessageRouterChannel")
    public HeaderValueRouter relayMessageHeaderValueRouter(BinderAwareChannelResolver binderAwareChannelResolver) {
        HeaderValueRouter router = new HeaderValueRouter(MessageRelayHandler.RELAY_DESTINATION);
        router.setDefaultOutputChannelName("nullChannel");
        router.setChannelResolver(binderAwareChannelResolver);
        router.setLoggingEnabled(true);
        return router;
    }

    @Bean
    public MessageRelayHandler domainEventHandlerRelay() {
        return new MessageRelayHandler(relayMessageRouterChannel());
    }
}
