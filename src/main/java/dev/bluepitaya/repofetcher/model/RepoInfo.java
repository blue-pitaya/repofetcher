package dev.bluepitaya.repofetcher.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RepoInfo {
  private String name;
  private String ownerLogin;
  private List<BranchInfo> branches;
}
