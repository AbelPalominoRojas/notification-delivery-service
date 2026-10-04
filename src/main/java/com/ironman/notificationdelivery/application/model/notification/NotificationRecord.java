package com.ironman.notificationdelivery.application.model.notification;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRecord {

  private String id;
  private String payerIdentification;
  private String transactionType;
  private String amountValue;
  private String currencyCode;
  private Instant deliveredAt;
}
