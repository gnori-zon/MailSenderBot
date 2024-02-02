package org.gnori.client.telegram.service;

import org.gnori.store.entity.MessageSentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageSentRecordRepository extends JpaRepository<MessageSentRecord, Long> {
}
