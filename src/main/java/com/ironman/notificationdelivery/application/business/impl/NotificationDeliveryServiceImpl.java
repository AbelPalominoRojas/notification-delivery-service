package com.ironman.notificationdelivery.application.business.impl;

import com.ironman.notificationdelivery.application.business.NotificationDeliveryService;
import com.ironman.notificationdelivery.application.exception.ApplicationException;
import com.ironman.notificationdelivery.application.exception.ExceptionCatalog;
import com.ironman.notificationdelivery.application.model.event.PaymentOrderInitiationTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class NotificationDeliveryServiceImpl implements NotificationDeliveryService {

  @Override
  public void deliver(PaymentOrderInitiationTransaction event) {
    try {
      var payerIdentification = extractPayerIdentification(event);
      var amount = event.getPaymentTransaction().getPaymentTransactionAmount();

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

  private String extractPayerIdentification(PaymentOrderInitiationTransaction event) {
    if (event == null
        || event.getPayerReference() == null
        || event.getPayerReference().getPartyIdentification() == null
        || event.getPayerReference().getPartyIdentification().getPartyIdentification() == null) {
      throw ExceptionCatalog.INVALID_EVENT_PAYLOAD.buildException();
    }

    return event
        .getPayerReference()
        .getPartyIdentification()
        .getPartyIdentification()
        .getIdentifierValue();
  }
}
