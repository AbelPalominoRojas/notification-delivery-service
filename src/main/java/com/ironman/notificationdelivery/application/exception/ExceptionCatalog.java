package com.ironman.notificationdelivery.application.exception;

import static com.ironman.notificationdelivery.application.exception.ApplicationException.ExceptionType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCatalog {
  INVALID_EVENT_PAYLOAD(
      "NODS0001",
      ExceptionType.BAD_REQUEST,
      "Invalid payment order initiation event payload: missing payer reference."),
  APPLICATION_ERROR(
      "NODS0002",
      ExceptionType.INTERNAL_SERVER_ERROR,
      "An unexpected error occurred, please try again later.");

  private final String code;
  private final ExceptionType exceptionType;
  private final String message;

  public ApplicationException buildException(Object... args) {
    String formattedMessage = String.format(message, args);

    return new ApplicationException(code, exceptionType, formattedMessage);
  }
}
