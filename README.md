# Repofetcher

This is simple spring boot application to fetch non-forekd repositories of given user, with information about braches and last commit sha.

## Running

To run server use command:
```
./gradlew bootRun
```

This will create server binded to port 8080.

## Example usage

To fetch information about user `blue-pitaya`:
```
curl -d "blue-pitaya" http://127.0.0.1:8080/
```

To fetch information with github api token (useful, because without it you are limited to 60 reqs/h):
```
curl -d "blue-pitaya" -H "Authorization: Bearer <YOUR_TOKEN>" http://127.0.0.1:8080/
```

To test that application requires proper accept header (like `Accept: application/json`) create request with different accept header:
```
curl -v -H "Accept: application/xml" -d "blue-pitaya" http://127.0.0.1:8080/
```

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
