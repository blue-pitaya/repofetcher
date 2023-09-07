package dev.bluepitaya.repofetcher.exceptions;

import dev.bluepitaya.repofetcher.model.ErrorInfo;
import lombok.Getter;

public class UserNotFoundException extends Exception {
  @Getter
  private ErrorInfo errorInfo;

  public UserNotFoundException() {
    this.errorInfo = new ErrorInfo(404, "User does not exists.");
  }
}
