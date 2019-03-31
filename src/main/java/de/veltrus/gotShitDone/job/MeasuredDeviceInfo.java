package de.veltrus.gotShitDone.job;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
class MeasuredDeviceInfo {
    private float watt;
    private LocalDateTime time;
    private int wattHours;

    static MeasuredDeviceInfo create(float watt, int wattHours) {
        return new MeasuredDeviceInfo(watt, LocalDateTime.now(), wattHours);
    }
}
