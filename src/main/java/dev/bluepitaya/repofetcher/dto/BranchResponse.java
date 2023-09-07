package dev.bluepitaya.repofetcher.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BranchResponse {
  private String name;
  private Commit commit;
}
