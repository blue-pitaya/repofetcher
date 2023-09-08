package dev.bluepitaya.repofetcher;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import dev.bluepitaya.repofetcher.handlers.AppHandler;

@Configuration
public class AppRouter {

  @Bean
  public RouterFunction<ServerResponse> routes(AppHandler handler) {
    return RouterFunctions.route()
        .POST("/", accept(MediaType.APPLICATION_JSON), handler::response)
        .POST("/", handler::invalidAcceptHeaderResponse)
        .build();
  }

  private RequestPredicate accept(MediaType mediaType) {
    return request -> {
      List<MediaType> acceptedMediaTypes = request.headers().accept();

      return acceptedMediaTypes.stream().anyMatch(mediaType::isCompatibleWith);
    };
  }

}
