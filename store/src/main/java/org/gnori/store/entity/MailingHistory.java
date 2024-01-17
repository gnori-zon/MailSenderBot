package org.gnori.store.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import lombok.*;
import org.gnori.store.entity.enums.StateMessage;

@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mailing_history")
public class MailingHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "state_last_message")
    private StateMessage stateLastMessage;

    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "mailing_history_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_mailing_history_id_from_message_sent_records")
    )
    private List<MessageSentRecord> mailingList = new ArrayList<>();

    @OneToOne(
            cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},
            mappedBy = "mailingHistory"
    )
    private Account account;

    @Override
    public String toString() {
        return "MailingHistory{id=%s}".formatted(id);
    }
}
