package dev.bluepitaya.repofetcher;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import dev.bluepitaya.repofetcher.model.ErrorInfo;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = AppHandler.class)
@Import(RouterConfig.class)
public class AppHandlerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockBean
  private WebClient.Builder webClientBuilder;

  @Test
  void whenHeaderIsApplicationXml_returnErrorResponse() {
    webTestClient.post()
        .uri("/")
        .accept(MediaType.APPLICATION_XML)
        .bodyValue("blue-pitaya")
        .exchange()
        .expectStatus().isEqualTo(406)
        .expectBody(ErrorInfo.class)
        .isEqualTo(new ErrorInfo(406, "Accept json header required."));
  }

  @Test
  void whenUserNotExists_returnErrorResponse() {
    var webClient = WebClient.builder()
        .exchangeFunction(clientRequest -> Mono.just(ClientResponse.create(HttpStatus.NOT_FOUND)
            .header("content-type", "application/json")
            .build()))
        .build();

    // TODO: this mock is not injected to AppHandler, so test dont work :(
    Mockito.when(webClientBuilder.build()).thenReturn(webClient);

    webTestClient.post()
        .uri("/")
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue("non-existent")
        .exchange()
        .expectStatus().isEqualTo(404)
        .expectBody(ErrorInfo.class)
        .isEqualTo(new ErrorInfo(404, "User does not exists."));
  }
}
