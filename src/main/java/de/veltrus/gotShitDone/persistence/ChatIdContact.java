package de.veltrus.gotShitDone.persistence;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "chatId")
@AllArgsConstructor
@NoArgsConstructor
public class ChatIdContact {

    @Id
    private Long chatId;

    private String number;
    private String firstName;
    private String lastName;
    private Integer userId;

}
