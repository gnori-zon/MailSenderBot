package org.gnori.mailsenderbot.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message_sent_records")
public class MessageSentRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long countMessages;
    @CreationTimestamp
    private LocalDateTime sendDate;

    @Override
    public String toString() {
        return "\nMessageSentRecord{" +
                "id=" + id +
                ", countMessages=" + countMessages +
                ", sendDate=" + sendDate +
                '}';
    }
}

