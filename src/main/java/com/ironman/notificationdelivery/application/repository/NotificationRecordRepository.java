package com.ironman.notificationdelivery.application.repository;

import com.ironman.notificationdelivery.application.model.notification.NotificationRecord;

public interface NotificationRecordRepository {

  /**
   * Persists the record only if no record with the same id exists yet, guarding delivery
   * idempotency against Kafka's at-least-once redelivery.
   *
   * @return {@code true} if the record was stored, {@code false} if it was already present.
   */
  boolean saveIfAbsent(NotificationRecord record);
}
