package de.veltrus.gotShitDone.telegram;

import de.veltrus.gotShitDone.persistence.ChatIdContact;
import de.veltrus.gotShitDone.persistence.ChatIdsRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Contact;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Getter
@Slf4j
public class KnownChatIds {

    @Autowired
    private ChatIdsRepository repo;

    public void addChatId(Long id, Contact contact) {
        log.info("Added chat id {}, number {}.", id, contact.getPhoneNumber());
        repo.save(new ChatIdContact(id, contact.getPhoneNumber(), contact.getFirstName(), contact.getLastName(), contact.getUserID()));
    }

    public List<Long> getChatIds() {
        return repo.findAll()
                .stream()
                .map(ChatIdContact::getChatId)
                .collect(Collectors.toList());
    }

    public Optional<ChatIdContact> getChatIdContact(Long id) {
        return repo.findById(id);
    }
}
