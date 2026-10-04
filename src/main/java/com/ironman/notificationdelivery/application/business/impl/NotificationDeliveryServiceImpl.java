package com.ironman.notificationdelivery.application.business.impl;

import com.ironman.notificationdelivery.application.business.NotificationDeliveryService;
import com.ironman.notificationdelivery.application.exception.ApplicationException;
import com.ironman.notificationdelivery.application.exception.ExceptionCatalog;
import com.ironman.notificationdelivery.application.model.event.Amount;
import com.ironman.notificationdelivery.application.model.event.PaymentOrderInitiationTransaction;
import com.ironman.notificationdelivery.application.model.notification.NotificationRecord;
import com.ironman.notificationdelivery.application.repository.NotificationRecordRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class NotificationDeliveryServiceImpl implements NotificationDeliveryService {

  private final NotificationRecordRepository notificationRecordRepository;

  @Override
  public void deliver(String notificationId, PaymentOrderInitiationTransaction event) {
    try {
      var payerIdentification = extractPayerIdentification(event);
      var amount = extractAmount(event);

      var record =
          NotificationRecord.builder()
              .id(notificationId)
              .payerIdentification(payerIdentification)
              .transactionType(String.valueOf(event.getPaymentTransactionType()))
              .amountValue(amount.getAmountValue())
              .currencyCode(String.valueOf(amount.getAmountCurrency().getAccountCurrencyType()))
              .deliveredAt(Instant.now())
              .build();

      if (!saveNotificationRecord(record)) {
        log.info("Notification {} was already delivered, skipping duplicate event", notificationId);
        return;
      }

      log.info(
          "Notification delivered to payer {} for a {} transaction of {} {}",
          payerIdentification,
          event.getPaymentTransactionType(),
          amount.getAmountValue(),
          amount.getAmountCurrency().getAccountCurrencyType());

    } catch (ApplicationException e) {
      throw e;
    } catch (Exception e) {
      log.error("Failed to deliver notification: {}", e.getMessage(), e);
      throw ExceptionCatalog.APPLICATION_ERROR.buildException();
    }
  }

  private boolean saveNotificationRecord(NotificationRecord record) {
    try {
      return notificationRecordRepository.saveIfAbsent(record);
    } catch (Exception e) {
      log.error(
          "Failed to persist notification record {} in Redis: {}",
          record.getId(),
          e.getMessage(),
          e);
      throw ExceptionCatalog.NOTIFICATION_PERSISTENCE_ERROR.buildException();
    }
  }

  private String extractPayerIdentification(PaymentOrderInitiationTransaction event) {
    if (event == null
        || event.getPayerReference() == null
        || event.getPayerReference().getPartyIdentification() == null
        || event.getPayerReference().getPartyIdentification().getPartyIdentification() == null) {
      throw ExceptionCatalog.INVALID_EVENT_PAYLOAD.buildException("payer reference");
    }

    return event
        .getPayerReference()
        .getPartyIdentification()
        .getPartyIdentification()
        .getIdentifierValue();
  }

  private Amount extractAmount(PaymentOrderInitiationTransaction event) {
    if (event.getPaymentTransaction() == null
        || event.getPaymentTransaction().getPaymentTransactionAmount() == null
        || event.getPaymentTransaction().getPaymentTransactionAmount().getAmountValue() == null
        || event.getPaymentTransaction().getPaymentTransactionAmount().getAmountCurrency() == null
        || event
                .getPaymentTransaction()
                .getPaymentTransactionAmount()
                .getAmountCurrency()
                .getAccountCurrencyType()
            == null) {
      throw ExceptionCatalog.INVALID_EVENT_PAYLOAD.buildException("payment transaction amount");
    }

    return event.getPaymentTransaction().getPaymentTransactionAmount();
  }
}
