package org.gnori.store.repository;

import org.gnori.store.entity.MessageSentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * dao for {@link MessageSentRecord} entity.
 */
public interface MessageSentRecordRepository extends JpaRepository<MessageSentRecord,Long> {
}
