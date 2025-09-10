package com.example.social_network_api.config;

import com.example.social_network_api.security.jwt.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JWTUtils jwtUtils;

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

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = accessor.getFirstNativeHeader("Authorization");
                    if (token != null && token.startsWith("Bearer ")) {
                        token = token.substring(7);
                        String username = jwtUtils.extractUsername(token);
                            accessor.setUser(() -> username); // set Principal
                    }
                }
                return message;
            }
        });
    }

}

// <--- client-side --->
//const socket = new SockJS('/ws');
//const stompClient = Stomp.over(socket);
//
//stompClient.connect(
//{ Authorization: 'Bearer ' + token }, // <-- bắt buộc
//function(frame) {
//    console.log('Connected', frame);
//
//    // subscribe private
//    stompClient.subscribe('/user/queue/messages', function(message) {
//      const body = JSON.parse(message.body);
//        console.log('Private message', body);
//    });
//
//    // gửi
//    stompClient.send('/app/chat.sendMessage', {}, JSON.stringify({
//            senderId: 'alice',
//            receiverId: 'bob', // username của người nhận
//            content: 'hello bob'
//    }));
//},
//function(error) {
//    console.error('STOMP connect error', error);
//}
//);

