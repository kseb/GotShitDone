package de.veltrus.gotShitDone.telegram;

import de.veltrus.gotShitDone.configuration.TelegramConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class GSDTelegramLongPollingBot extends TelegramLongPollingBot {

    private TelegramConfig config;
    private KnownChatIds chatIds;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage() == null) {
            return;
        }
        if (update.getMessage().getText() != null && update.getMessage().getText().equals(("/start"))) {
            SendMessage sendMessage = new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText("Please validate your number.");
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(true);

            List<KeyboardRow> keyboard = new ArrayList<>();
            KeyboardRow keyboardFirstRow = new KeyboardRow();
            KeyboardButton keyboardButton = new KeyboardButton();
            keyboardButton.setText("Share your number >").setRequestContact(true);
            keyboardFirstRow.add(keyboardButton);
            keyboard.add(keyboardFirstRow);
            replyKeyboardMarkup.setKeyboard(keyboard);

            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.getMessage().getContact() != null &&
                config.getAllowedNumbers()
                    .stream()
                    .map(n -> StringUtils.replaceAll(n, "^0?[^0-9]*", StringUtils.EMPTY))
                    .collect(Collectors.toList())
                .contains(update.getMessage().getContact().getPhoneNumber())) {
            Long chatId = update.getMessage().getChatId();
            Contact contact = update.getMessage().getContact();
            if (chatIds.getChatIdContact(chatId).isPresent()) {
                log.info("Chat id {} with number {} already known.", chatId, contact.getPhoneNumber());
                sendMessage(chatId, "I already know your number.");
            } else {
                sendMessage(chatId, "You successfully verified your number.");
                chatIds.addChatId(chatId, contact);
                log.info("Validated number {} with chat id {}.", contact.getPhoneNumber(), chatId);
            }
        } else {
            sendMessage(update.getMessage().getChatId(), "Your number could not be verified. Try again.");
            log.warn("Error validating number with chat id {}.", update.getMessage().getChatId());
        }
    }

    private void sendMessage(Long id, String text) {
        try {
            execute(new SendMessage(id, text));
        } catch (TelegramApiException e) {
            log.error("Error sending message.", e);
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }
}
