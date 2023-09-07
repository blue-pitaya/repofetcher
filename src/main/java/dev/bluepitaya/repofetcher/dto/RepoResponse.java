package dev.bluepitaya.repofetcher.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RepoResponse {
  private String name;
  private boolean fork;
  private Owner owner;
}
