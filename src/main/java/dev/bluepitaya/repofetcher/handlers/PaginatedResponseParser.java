package dev.bluepitaya.repofetcher.handlers;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientResponse;

// <https://api.github.com/user/170270/repos?page=29>; rel="prev", <https://api.github.com/user/170270/repos?page=31>; rel="next", <https://api.github.com/user/170270/repos?page=36>; rel="last", <https://api.github.com/user/170270/repos?page=1>; rel="first"

public class PaginatedResponseParser {

  private ClientResponse response;

  public PaginatedResponseParser(ClientResponse response) {
    this.response = response;
  }

  public Optional<String> getNextLink() {
    var linkHeaders = response.headers().header(HttpHeaders.LINK);
    var firstLinkHeader = linkHeaders.stream().findFirst();

    return firstLinkHeader.flatMap(v -> parseNextLink(v));
  }

  private Optional<String> parseNextLink(String headerValue) {
    Pattern p = Pattern.compile("<(.+?)>; rel=\"(.+?)\"");
    Matcher m = p.matcher(headerValue);

    while (m.find()) {
      var linkPart = m.group(1);
      var paramPart = m.group(2);

      if (linkPart == null || paramPart == null) {
        continue;
      }

      if (paramPart.equals("next")) {
        return Optional.of(linkPart);
      }
    }

    return Optional.empty();
  }

}
