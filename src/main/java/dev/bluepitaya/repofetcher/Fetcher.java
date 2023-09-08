package dev.bluepitaya.repofetcher;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerResponse;

import dev.bluepitaya.repofetcher.dto.BranchResponse;
import dev.bluepitaya.repofetcher.dto.RepoResponse;
import dev.bluepitaya.repofetcher.exceptions.UserNotFoundException;
import dev.bluepitaya.repofetcher.model.BranchInfo;
import dev.bluepitaya.repofetcher.model.ErrorInfo;
import dev.bluepitaya.repofetcher.model.RepoInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Fetcher {
  private String username;
  private Optional<String> authHeaderValue;
  private WebClient webClient;

  public Fetcher(String username, Optional<String> authHeaderValue, WebClient webClient) {
    this.username = username;
    this.authHeaderValue = authHeaderValue;
    this.webClient = webClient;
  }

  public Mono<ServerResponse> fetch() {
    String uri = String.format("https://api.github.com/users/%s/repos?per_page=100", this.username);

    return fetchReposRecursive(uri, Flux.empty())
        .filter(item -> !item.isFork())
        .flatMap(item -> {
          Mono<RepoInfo> repoInfo = getBranches(item.getName())
              .map(branches -> new RepoInfo(item.getName(), item.getOwner().getLogin(), branches));

          return Flux.from(repoInfo);
        })
        .collectList()
        .flatMap(response -> ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(response))
        .onErrorResume(UserNotFoundException.class, e -> userNotFoundResponse(e))
        .onErrorResume(WebClientResponseException.class, e -> githubApiErrorResponse(e));
  }

  private Flux<RepoResponse> fetchReposRecursive(String uri, Flux<RepoResponse> accumulatedItems) {
    return fetchFromGithubApi(uri)
        .exchangeToFlux(response -> {
          if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
            return Flux.error(new UserNotFoundException());
          }

          var currentItems = response.bodyToFlux(RepoResponse.class);
          var nextAcc = Flux.merge(accumulatedItems, currentItems);

          var nextLinkOptional = new PaginatedResponseParser(response).getNextLink();
          if (nextLinkOptional.isPresent()) {
            return fetchReposRecursive(nextLinkOptional.get(), nextAcc);
          }

          return nextAcc;
        });
  }

  private Mono<List<BranchInfo>> getBranches(String repoName) {
    var uri = String.format("https://api.github.com/repos/%s/%s/branches", this.username, repoName);

    return fetchFromGithubApi(uri)
        .retrieve()
        .bodyToFlux(BranchResponse.class)
        .map(item -> new BranchInfo(item.getName(), item.getCommit().getSha()))
        .collectList();
  }

  private RequestHeadersSpec<?> fetchFromGithubApi(String uri) {
    var request = webClient.get()
        .uri(uri)
        .header("Accept", "application/vnd.github+json")
        .header("X-GitHub-Api-Version", "2022-11-28");

    return (this.authHeaderValue.isPresent() ? request.header("Authorization", this.authHeaderValue.get()) : request);
  }

  private Mono<ServerResponse> userNotFoundResponse(UserNotFoundException e) {
    return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(e.getErrorInfo());
  }

  private Mono<ServerResponse> githubApiErrorResponse(WebClientResponseException e) {
    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .bodyValue(new ErrorInfo(500, String.format("Error from github api: %s", e.getMessage())));
  }
}
