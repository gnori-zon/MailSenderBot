package org.gnori.store.entity;


import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gnori.store.entity.enums.State;

@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "key_for_mail")
    private String keyForMail;

    @OneToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "mailing_history_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_mailing_history_id_from_account" )
    )
    private MailingHistory mailingHistory;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state;

    @Override
    public String toString() {
        return "Account{id=%s}".formatted(id);
    }
}

