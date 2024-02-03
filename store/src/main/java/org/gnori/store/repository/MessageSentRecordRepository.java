package org.gnori.store.repository;

import org.gnori.store.entity.MessageSentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageSentRecordRepository extends JpaRepository<MessageSentRecord, Long> {
}
