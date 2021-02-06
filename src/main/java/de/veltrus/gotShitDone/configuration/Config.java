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
@Setter
@Slf4j
@ToString
public class Config {

    private String url;
    private String user;
    private String password;

    @Getter
    private List<DeviceConfig> deviceConfigs;
    @Getter
    private HomeAutomation fritz;

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

    public HomeAutomation fritz() {
        if (fritz == null) {
            fritz = connect();
        }
        return fritz;
    }

    private HomeAutomation connect() {
        return HomeAutomation.connect(url, user, password);
    }

    public void reconnectToFritzBox() {
        fritz.logout();
        fritz = null;
        fritz();
    }
}
