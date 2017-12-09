package de.veltrus.gotShitDone.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "telegram")
@Getter
@Setter
@ToString
public class TelegramConfig {
    private String token;
    private String botUsername;
    private List<String> allowedNumbers;
}
