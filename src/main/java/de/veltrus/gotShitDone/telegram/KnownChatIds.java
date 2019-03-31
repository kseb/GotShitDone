package de.veltrus.gotShitDone.telegram;

import de.veltrus.gotShitDone.persistence.ChatIdContact;
import de.veltrus.gotShitDone.persistence.ChatIdsRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Contact;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Getter
@RequiredArgsConstructor
@Slf4j
public class KnownChatIds {

    private final ChatIdsRepository repo;

    void addChatId(Long id, Contact contact) {
        log.info("Added chat id {}, number {}.", id, contact.getPhoneNumber());
        repo.save(new ChatIdContact(id, contact.getPhoneNumber(), contact.getFirstName(), contact.getLastName(), contact.getUserID()));
    }

    public List<Long> getChatIds() {
        return repo.findAll()
                .stream()
                .map(ChatIdContact::getChatId)
                .collect(Collectors.toList());
    }

    Optional<ChatIdContact> getChatIdContact(Long id) {
        return repo.findById(id);
    }
}
