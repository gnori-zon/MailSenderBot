package org.gnori.data.repository;

import org.gnori.data.entity.MessageSentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageSentRecordRepository extends JpaRepository<MessageSentRecord, Long> {
}
