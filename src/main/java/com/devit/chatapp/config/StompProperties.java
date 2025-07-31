package com.devit.chatapp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "stomp")
public class StompProperties {
    private String host;
    private int port;
    private String login;
    private String passcode;
}
