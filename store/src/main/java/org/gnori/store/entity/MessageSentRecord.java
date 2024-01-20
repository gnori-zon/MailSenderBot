package org.gnori.store.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message_sent_records")
public class MessageSentRecord implements Comparable<MessageSentRecord>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="count_messages")
    private Integer countMessages;

    @Column(name="sent_date")
    @CreationTimestamp
    private LocalDateTime sentDate;

    @Override
    public String toString() {
        return "MessageSentRecord{id=%s".formatted(id);
    }

    @Override
    public int compareTo(MessageSentRecord o) {
        return getSentDate().compareTo(o.getSentDate());
    }
}

