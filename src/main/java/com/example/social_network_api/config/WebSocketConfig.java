package com.example.social_network_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Broker cho client subscribe, kênh chung hoặc user riêng
        config.enableSimpleBroker("/topic", "/queue");
        // Prefix client gửi message lên server
        config.setApplicationDestinationPrefixes("/app");
        // Prefix cho kênh riêng user
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint để client connect WebSocket, enable SockJS nếu cần fallback
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
}
