package org.gnori.mailsenderbot.entity;

import lombok.*;
import org.gnori.mailsenderbot.entity.enums.UserState;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String mailForDistribution;
    private String keyForMail;
    private boolean isActive;
    @Enumerated(EnumType.STRING)
    private UserState state;
    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private MailingHistory mailingHistory;

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", mailForDistribution='" + mailForDistribution + '\'' +
                ", keyForMail='" + keyForMail + '\'' +
                ", isActive=" + isActive +
                ", state=" + state +
                ", mailingHistory=" + mailingHistory +
                '}';
    }
}
