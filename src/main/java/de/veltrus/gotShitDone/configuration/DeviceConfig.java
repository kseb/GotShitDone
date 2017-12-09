package de.veltrus.gotShitDone.configuration;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DeviceConfig {
    private String name;
    private long standbyInWatt;
    private long waitInSeconds;
    private String startMessage;
    private String stopMessage;
}
