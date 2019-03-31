package de.veltrus.gotShitDone.job;

import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@ToString
public class LatestDeviceInfos {

    private final Map<String, MeasuredDeviceInfo> deviceInfos = new HashMap<>();

    Optional<MeasuredDeviceInfo> getDeviceInfoForDeviceName(String name) {
        return Optional.ofNullable(deviceInfos.get(name));
    }

    void putDeviceInfo(String deviceName, MeasuredDeviceInfo info) {
        deviceInfos.put(deviceName, info);
    }

    MeasuredDeviceInfo removeDeviceInfo(String deviceName) {
        return deviceInfos.remove(deviceName);
    }

}
