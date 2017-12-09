package de.veltrus.gotShitDone.telegram;

import de.veltrus.gotShitDone.configuration.TelegramConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import javax.annotation.PostConstruct;

@Component
public class Telegram {

    @Autowired
    private TelegramConfig config;

    @Autowired
    KnownChatIds chatIds;

    private GSDTelegramLongPollingBot bot;

    @Bean
    public GSDTelegramLongPollingBot getBot() {
        return bot;
    }

    @PostConstruct
    public void init() throws TelegramApiRequestException {
        ApiContextInitializer.init();
        TelegramBotsApi api = new TelegramBotsApi();
        bot = new GSDTelegramLongPollingBot(config, chatIds);
        api.registerBot(bot);
    }

}
