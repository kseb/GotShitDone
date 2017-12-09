package de.veltrus.gotShitDone.configuration;

import com.github.kaklakariada.fritzbox.HomeAutomation;
import com.github.kaklakariada.fritzbox.model.homeautomation.Device;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
@ToString
public class Config {

    private String url;
    private String user;
    private String password;

    private List<DeviceConfig> deviceConfigs;

    public Optional<DeviceConfig> getDeviceConfigByName(String name) {
        return deviceConfigs.stream().filter(config -> name.equals(config.getName())).findAny();
    }

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

    @Bean
    public HomeAutomation fritz() {
        return HomeAutomation.connect(this.getUrl(), this.getUser(), this.getPassword());
    }
}