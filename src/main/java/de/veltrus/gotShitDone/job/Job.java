package de.veltrus.gotShitDone.job;

import com.github.kaklakariada.fritzbox.model.homeautomation.Device;
import de.veltrus.gotShitDone.configuration.DeviceConfig;
import de.veltrus.gotShitDone.telegram.GSDTelegramLongPollingBot;
import de.veltrus.gotShitDone.telegram.KnownChatIds;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
public class Job implements Runnable {

    private final LatestDeviceInfos latestDeviceInfos;
    private final GSDTelegramLongPollingBot bot;
    private Device device;
    private DeviceConfig deviceConfig;
    private KnownChatIds chatIds;

    public Job(Device device, DeviceConfig deviceConfig, LatestDeviceInfos latestDeviceInfos, GSDTelegramLongPollingBot bot, KnownChatIds chatIds) {
        this.deviceConfig = deviceConfig;
        this.device = device;
        this.latestDeviceInfos = latestDeviceInfos;
        this.bot = bot;
        this.chatIds = chatIds;
    }

    @Override
    public void run() {
        Optional<MeasuredDeviceInfo> info = latestDeviceInfos.getDeviceInfoForDeviceName(device.getName());
        float powerWatt = device.getPowerMeter().getPowerWatt();
        int endWattHours = device.getPowerMeter().getEnergyWattHours();
        if (powerWatt > deviceConfig.getStandbyInWatt()) {
            if (!info.isPresent()) {
                int energyWattHours = device.getPowerMeter().getEnergyWattHours();
                latestDeviceInfos.putDeviceInfo(device.getName(),
                        MeasuredDeviceInfo.create(powerWatt, energyWattHours));
                sendMessage(deviceConfig.getStartMessage());
                log.info("Device {} started working.", deviceConfig.getName());
            } else {
                latestDeviceInfos.putDeviceInfo(device.getName(),
                        MeasuredDeviceInfo.create(powerWatt,
                                latestDeviceInfos.getDeviceInfoForDeviceName(device.getName()).get().getWattHours()));
            }
        } else if (info.isPresent() && powerWatt >= 0 && endWattHours > 0 && deviceConfig.getWaitInSeconds() < Duration.between(info.get().getTime(), LocalDateTime.now()).getSeconds()) {
            MeasuredDeviceInfo measuredDeviceInfo = latestDeviceInfos.removeDeviceInfo(deviceConfig.getName());
            int startWattHours = measuredDeviceInfo.getWattHours();
            int consumption = endWattHours - startWattHours;
            double cost = consumption / 1000. * deviceConfig.getPricePerKwh();
            String costString = String.format("%.2f", cost);
            sendMessage(StringUtils.replace(StringUtils.replace(deviceConfig.getStopMessage(), "{COST}", costString), "{WH}", String.valueOf(consumption)));
            log.info("Device {} is ready.", deviceConfig.getName());
        }
    }

    private void sendMessage(String text) {
        if (chatIds.getChatIds().isEmpty()) {
            log.error("No chat id present. Please start a conversation with the bot and type in the configured token.");
            return;
        }
        chatIds.getChatIds()
                .forEach(id -> {
                    try {
                        bot.execute(new SendMessage(id, text));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                });
    }
}
