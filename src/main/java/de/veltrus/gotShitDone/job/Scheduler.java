package de.veltrus.gotShitDone.job;

import com.github.kaklakariada.fritzbox.FritzBoxException;
import com.github.kaklakariada.fritzbox.HomeAutomation;
import com.github.kaklakariada.fritzbox.model.homeautomation.Device;
import de.veltrus.gotShitDone.configuration.Config;
import de.veltrus.gotShitDone.telegram.GSDTelegramLongPollingBot;
import de.veltrus.gotShitDone.telegram.KnownChatIds;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduler {

    private static final int TIMEOUT_IN_SECONDS = 5000;

    private final Config config;

    private final HomeAutomation fritz;

    private final LatestDeviceInfos latestDeviceInfos;

    private final GSDTelegramLongPollingBot bot;

    private final KnownChatIds chatIds;

    @Scheduled(fixedDelayString = "${intervalInMS}")
    public void init() {
        List<Device> deviceListInfosFritzbox;
        try {
            deviceListInfosFritzbox = fritz.getDeviceListInfos().getDevices();
        } catch (FritzBoxException e) {
            log.error("Error reaching or logging in to fritz box. Trying to reconnect.");
            config.reconnect();
            return;
        }
        List<Job> jobs = config.getDeviceConfigs()
                .stream()
                .map(deviceConfig -> new Job(deviceListInfosFritzbox
                        .stream()
                        .filter(device -> device.getName().equals(deviceConfig.getName()))
                        .findAny().orElseThrow(() -> new RuntimeException("Invalid device name " + deviceConfig.getName() + " configured.")),
                    deviceConfig, latestDeviceInfos, bot, chatIds))
                .collect(Collectors.toList());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        jobs.forEach(executor::submit);
        executor.shutdown();
        try {
            executor.awaitTermination(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("At least one job took too much time.");
        }
    }

}
