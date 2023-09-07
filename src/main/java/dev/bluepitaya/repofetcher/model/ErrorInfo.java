package dev.bluepitaya.repofetcher.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorInfo {
  private int status;
  private String message;
}
