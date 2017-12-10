package de.veltrus.gotShitDone.job;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MeasuredDeviceInfo {
    private float watt;
    private LocalDateTime time;
    private int wattHours;

    public static MeasuredDeviceInfo create(float watt, int wattHours) {
        return new MeasuredDeviceInfo(watt, LocalDateTime.now(), wattHours);
    }
}
