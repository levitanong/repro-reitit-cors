# Usage

## Start a server

To start a problematic server:
```
> clj -M:problematic
```

To start a server with workaround:
```
> clj -M:workaround
```

## Test the http requests

Make the following http requests.
If you use emacs, you may use `repro.http` with emacs restclient for your convenience.

Preflight, wrong origin.
- app-with-problem: correctly does not have CORS headers
- app-with-workaround: correctly does not have CORS headers

```
OPTIONS http://localhost:5000/v1/foo
Access-Control-Request-Method: PUT
Origin: http://example.com
```

Preflight, correct origin
- app-with-problem: INCORRECTLY does not have CORS headers
- app-with-workaround: correctly has CORS headers

```
OPTIONS http://localhost:5000/v1/foo
Access-Control-Request-Method: PUT
Origin: http://localhost:5000
```
