package org.gnori.store.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message_sent_records")
public class MessageSentRecord implements Comparable<MessageSentRecord>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer countMessages;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageSentRecord that = (MessageSentRecord) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }


    @Override
    public int compareTo(MessageSentRecord o) {
        return getSendDate().compareTo(o.getSendDate());
    }
}

