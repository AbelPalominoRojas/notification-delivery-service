package com.ironman.notificationdelivery.application.kafka;

import com.ironman.notificationdelivery.application.business.NotificationDeliveryService;
import com.ironman.notificationdelivery.application.model.event.PaymentOrderInitiationTransaction;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.reactive.messaging.kafka.api.IncomingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class PaymentOrderInitiationConsumer {

  private final NotificationDeliveryService notificationDeliveryService;

  @Blocking
  @Incoming("payment-order-initiation-in")
  public CompletionStage<Void> consume(Message<PaymentOrderInitiationTransaction> message) {
    try {
      notificationDeliveryService.deliver(resolveNotificationId(message), message.getPayload());

      return message.ack();
    } catch (Exception e) {
      log.error("Failed to process payment order initiation event: {}", e.getMessage(), e);

      return message.nack(e);
    }
  }

  private String resolveNotificationId(Message<PaymentOrderInitiationTransaction> message) {
    return message
        .getMetadata(IncomingKafkaRecordMetadata.class)
        .map(IncomingKafkaRecordMetadata::getKey)
        .filter(Objects::nonNull)
        .map(Object::toString)
        .orElseGet(() -> UUID.randomUUID().toString());
  }
}
