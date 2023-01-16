package org.gnori.mailsenderbot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mailing_histories")
public class MailingHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    private List<MessageSentRecord> mailingList;
    @OneToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "account_id")
    private Account account;

    @Override
    public String toString() {
        return "MailingHistory{" +
                "id=" + id +
                ", account=" + account.getId() +
                '}';
    }
}
