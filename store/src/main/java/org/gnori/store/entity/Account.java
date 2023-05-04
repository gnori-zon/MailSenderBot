package org.gnori.store.entity;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gnori.store.entity.enums.State;

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
