package com.ironman.notificationdelivery.application.business;

import com.ironman.notificationdelivery.application.model.event.PaymentOrderInitiationTransaction;

public interface NotificationDeliveryService {

  void deliver(PaymentOrderInitiationTransaction event);
}
