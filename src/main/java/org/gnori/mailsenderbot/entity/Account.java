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
    private Long id;
    @Column(unique = true)
    private String email;
    private String keyForMail;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "mailing_history_id", foreignKey = @ForeignKey(name = "FK_mailing_history_id_from_account" ))
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
