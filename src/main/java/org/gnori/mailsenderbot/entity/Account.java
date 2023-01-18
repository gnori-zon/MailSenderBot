package org.gnori.mailsenderbot.entity;

import lombok.*;
import org.gnori.mailsenderbot.entity.enums.State;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account")
public class Account {
    @Id
    //TODO chatId telegram
    private Long id;
    @Column(unique = true)
    private String email;
    private String keyForMail;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "mailing_history_id")
    private MailingHistory mailingHistory;

    private State state;

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", keyForMail='" + keyForMail + '\'' +
                ", mailingHistory=" + mailingHistory +
                ", state=" + state +
                '}';
    }
}
