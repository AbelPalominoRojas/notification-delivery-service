package com.ironman.notificationdelivery.application.kafka.deserializer;

import com.ironman.notificationdelivery.application.model.event.PaymentOrderInitiationTransaction;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class PaymentOrderInitiationTransactionDeserializer
    extends ObjectMapperDeserializer<PaymentOrderInitiationTransaction> {

  public PaymentOrderInitiationTransactionDeserializer() {
    super(PaymentOrderInitiationTransaction.class);
  }
}
