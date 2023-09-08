package dev.bluepitaya.repofetcher;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import dev.bluepitaya.repofetcher.model.ErrorInfo;
import reactor.core.publisher.Mono;

@Component
public class AppHandler {

  private final WebClient webClient;

  public AppHandler(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.build();
  }

  public Mono<ServerResponse> invalidAcceptHeaderResponse(ServerRequest request) {
    HttpStatus status = HttpStatus.NOT_ACCEPTABLE;
    ErrorInfo errorInfo = new ErrorInfo(status.value(), "Accept json header required.");

    return ServerResponse.status(status).bodyValue(errorInfo);
  }

  public Mono<ServerResponse> response(ServerRequest request) {
    Optional<String> authHeaderValue = request.headers().header("Authorization").stream().findFirst();

    return request.bodyToMono(String.class)
        .flatMap(username -> new Fetcher(username, authHeaderValue, webClient).fetch());
  }
}
