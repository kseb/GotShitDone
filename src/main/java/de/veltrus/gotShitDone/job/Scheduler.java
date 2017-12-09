package de.veltrus.gotShitDone.job;

import com.github.kaklakariada.fritzbox.HomeAutomation;
import com.github.kaklakariada.fritzbox.model.homeautomation.Device;
import de.veltrus.gotShitDone.configuration.Config;
import de.veltrus.gotShitDone.configuration.TelegramConfig;
import de.veltrus.gotShitDone.telegram.GSDTelegramLongPollingBot;
import de.veltrus.gotShitDone.telegram.KnownChatIds;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Scheduler {

    private static final int TIMEOUT_IN_SECONDS = 5000;

    @Autowired
    TelegramConfig telegramConfig;

    @Autowired
    private Config config;

    @Autowired
    private HomeAutomation fritz;

    @Autowired
    private LatestDeviceInfos latestDeviceInfos;

    @Autowired
    private GSDTelegramLongPollingBot bot;

    @Autowired
    private KnownChatIds chatIds;

    @Scheduled(fixedDelayString = "${intervalInMS}")
    public void init() {
        List<Device> deviceListInfosFritzbox = fritz.getDeviceListInfos().getDevices();
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
