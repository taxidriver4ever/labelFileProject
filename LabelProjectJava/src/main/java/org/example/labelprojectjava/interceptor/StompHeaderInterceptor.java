package org.example.labelprojectjava.interceptor;

import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.labelprojectjava.redis.UserInformationRedis;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpAttributes;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.UUID;

@Component
@Slf4j
public class StompHeaderInterceptor implements ChannelInterceptor {

    @Resource
    private UserInformationRedis userInformationRedis;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

            String userUUID = accessor.getFirstNativeHeader("userUUID");
            String loginUUID = accessor.getFirstNativeHeader("loginUUID");

            log.info(accessor.toString());

            if (userUUID != null && loginUUID != null && userInformationRedis.getUserUUID(userUUID).equals(loginUUID)) {

                accessor.setUser(new Principal() {
                    @Override
                    public String getName() {
                        return userUUID;
                    }
                });
            }

        }

        return message;
    }
}
