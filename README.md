# Repofetcher

This is simple spring boot application to fetch non-forekd repositories of given user, with information about braches and last commit sha.

## Running

To run server use command:
```
./gradlew bootRun
```

This will create server binded to port 8080.

## Example usage

To fetch information about user `blue-pitaya` run:
```
curl -d "blue-pitaya" http://127.0.0.1:8080/
```

To test that application requires proper accept header (like `Accept: application/json`) create request with different accept header:
```
curl -v -H "Accept: application/xml" -d "blue-pitaya" http://127.0.0.1:8080/
```

## Possible problems

Github api has rate limis for requests. For unauthenticated requests, the rate limit allows for up to 60 requests per hour. So you can hit the limit pretty fast when testing. To bypass it, you can use your own apiToken, by chaning code in `AppHandler`:
```
//change it to true and provide your apiToken to increase rate limit
private final boolean useApiToken = false;
private final String apiToken = "";
```

There are no specific "schema" checks for JSON response from github. If github decide to change their API, there can be some errors.

## Acceptance criteria

As an api consumer, given username and header “Accept: application/json”, I would like to list all his github repositories, which are not forks. Information, which I require in the response, is:

- Repository Name
- Owner Login
- For each branch it’s name and last commit sha

As an api consumer, given not existing github user, I would like to receive 404 response in such a format:
```
{
    “status”: ${responseCode}
    “Message”: ${whyHasItHappened}
}
```

As an api consumer, given header “Accept: application/xml”, I would like to receive 406 response in such a format:
```
{
    “status”: ${responseCode}
    “Message”: ${whyHasItHappened}
}
```

Please use https://developer.github.com/v3 as a backing API
