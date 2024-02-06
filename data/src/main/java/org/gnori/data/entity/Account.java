package org.gnori.data.entity;


import lombok.*;
import org.gnori.data.entity.enums.State;

import javax.persistence.*;

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

    @Column(name = "chat_id", unique = true, nullable = false)
    private Long chatId;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "username")
    private String username;

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

