package de.veltrus.gotShitDone.configuration;

import com.github.kaklakariada.fritzbox.HomeAutomation;
import com.github.kaklakariada.fritzbox.model.homeautomation.Device;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "fritzbox")
@Getter
@Setter
@Slf4j
@ToString
public class Config {

    private String url;
    private String user;
    private String password;

    private List<DeviceConfig> deviceConfigs;

    @PostConstruct
    public void validate() {
        List<Device> devices = fritz().getDeviceListInfos().getDevices();
        List<String> deviceNames = devices.stream().map(Device::getName).collect(Collectors.toList());
        Optional<DeviceConfig> invalidConfig = deviceConfigs.stream()
                .filter(config -> !deviceNames.contains(config.getName()))
                .findAny();
        if (invalidConfig.isPresent()) {
            throw new RuntimeException("No device found for configured device name " + invalidConfig.get().getName());
        }
    }

    @Bean(destroyMethod = "logout")
    public HomeAutomation fritz() {
        log.info("Logging in to {} with user {}.", this.getUrl(), this.getUser());
        return HomeAutomation.connect(this.getUrl(), this.getUser(), this.getPassword());
    }

    public void reconnectToFritzBox() {
        fritz();
    }
}
