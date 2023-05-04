package org.gnori.client.telegram.service;

import org.gnori.store.entity.MessageSentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * dao for {@link MessageSentRecord} entity.
 */
public interface MessageSentRecordRepository extends JpaRepository<MessageSentRecord,Long> {
}
