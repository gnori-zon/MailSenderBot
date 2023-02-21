package org.gnori.mailsenderbot.entity;

import lombok.*;
import org.gnori.mailsenderbot.entity.enums.StateMessage;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mailing_history")
public class MailingHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private StateMessage stateLastMessage;

    @OneToOne(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},
            mappedBy = "mailingHistory")
    private Account account;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "mailing_history_id", foreignKey = @ForeignKey(name = "FK_mailing_history_id_from_message_sent_records"))
    private List<MessageSentRecord> mailingList;

    @Override
    public String toString() {
        return "MailingHistory{" +
                "id=" + id +
                ", mailingList=" + mailingList +
                '}';
    }
}
