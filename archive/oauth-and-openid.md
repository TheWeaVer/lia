# Introduction

This article summarize OAuth 2.0 with Bearer Token ([RFC 6749](https://datatracker.ietf.org/doc/html/rfc6749) /
[RFC 6750](https://datatracker.ietf.org/doc/html/rfc6750)) and [OpenID Connect](https://openid.net/).

# OAuth 2.0

OAuth 2.0 is **authorization** framework to give accessibility to client with access token. Access token has a
specific scope, lifetime, and other access attributes. The interaction between the authorization server and
resource server is beyond the scope of OAuth.

```
     +--------+                               +---------------+
     |        |--(A)- Authorization Request ->|   Resource    |
     |        |                               |     Owner     |
     |        |<-(B)-- Authorization Grant ---|               |
     |        |                               +---------------+
     |        |
     |        |                               +---------------+
     |        |--(C)-- Authorization Grant -->| Authorization |
     | Client |                               |     Server    |
     |        |<-(D)----- Access Token -------|               |
     |        |                               +---------------+
     |        |
     |        |                               +---------------+
     |        |--(E)----- Access Token ------>|    Resource   |
     |        |                               |     Server    |
     |        |<-(F)--- Protected Resource ---|               |
     +--------+                               +---------------+
```

(A)  The client requests authorization from the resource owner. The authorization request can be made directly
to the resource owner(as shown), or preferably indirectly via the authorization server as an intermediary.

(B)  The client receives an authorization grant, which is a credential representing the resource owner's
authorization, expressed using one of four grant types defined in this specification or using an extension grant
type. The authorization grant type depends on the method used by the client to request authorization and the
types supported by the authorization server.

(C)  The client requests an access token by authenticating with the authorization server and presenting the
authorization grant.

(D)  The authorization server authenticates the client and validates the authorization grant, and if valid,
issues an access token.

(E)  The client requests the protected resource from the resource server and authenticates by presenting
the access token.

(F)  The resource server validates the access token, and if valid, serves the request.

With this process

* Client doesn't need to request with Resource Owner's credential.
* Resource Server doesn't need to authenticate user always.

# Obtaining Authorization

## Authorization Code Grant

This grant type is used to obtain access token and refresh token. It's important to create redirection-based
flow with confidential client.

* Resource owner: Target to grant to access resource to the client
* User-agent: User agent is a browser or mobile application via which the resource owner communicates to the
  authorization server.
* Client: Client is the application code that wants to access the resources of the user on the resource server.
  Client can be a web browser, native client or web server. If the client is on the server, it's considered a
  confidential client. If not it's public client.

```
     +----------+
     | Resource |
     |   Owner  |
     |          |
     +----------+
          ^
          |
         (B)
     +----|-----+          Client Identifier      +---------------+
     |         -+----(A)-- & Redirection URI ---->|               |
     |  User-   |                                 | Authorization |
     |  Agent  -+----(B)-- User authenticates --->|     Server    |
     |          |                                 |               |
     |         -+----(C)-- Authorization Code ---<|               |
     +-|----|---+                                 +---------------+
       |    |                                         ^      v
      (A)  (C)                                        |      |
       |    |                                         |      |
       ^    v                                         |      |
     +---------+                                      |      |
     |         |>---(D)-- Authorization Code ---------'      |
     |  Client |          & Redirection URI                  |
     |         |                                             |
     |         |<---(E)----- Access Token -------------------'
     +---------+       (w/ Optional Refresh Token)
```

(A) User-agent initiates the flow with their client identifier by redirecting resource owner's user-agent to
authorization server's endpoint.

(B) The authorization server authenticates the resource owner and establishes whether the resource owner grants
or denies the client's authorization request.

(C) Assuming that resource owner grants the client, authorization server redirects the user-agent to the client
with authorization code.

(D) Client requests an access token with the given code and used redirect uri in previous step. The
authorization server verifies and authenticates the made request.

* The authorization server authenticates the client by matching the URI used to redirect the client in
  step (C).
* It is compared whether the client acquiring authorization code and the client acquiring access token are the
  same.

(E) If the (D) is valid, authorization server respond back with an access token and, optionally, refresh token.

* When the client tries to get access token with refresh token, the authorization server does not request
  this authorization code grant.

## Implicit Grant

It grants for the client which can not manage credential such as single page application. It's grant without
code and give a authorization. So, authorization must have short lifetime and authorization server must not
returns refresh token. This method is risky way so, OAuth 2.1 introduces PKCE.

### OAuth 2.1 does not recommend Implicit Grant

OAuth 2.0 supports [Implicit Grant](https://datatracker.ietf.org/doc/html/rfc6749#section-1.3.2), however, OAuth
2.1 doesn't recommend it but recommend trying authorization request
with [PKCE](https://datatracker.ietf.org/doc/html/draft-ietf-oauth-v2-1-01#oauth-2-0-differences). Moreover,
there are other considerations in OAuth flow. OAuth 2.1
already expired, however, those approaches are problem to think about when providing OAuth flow.

## Client Credentials Grant

The client can request an access token by its credentials for their protected resources. The another resource
owner can request access token if this resource owner is arranged by the authorization server. But it's beyond
of the scope of the oauth 2.0 specification.

```
     +---------+                                  +---------------+
     |         |                                  |               |
     |         |>--(A)- Client Authentication --->| Authorization |
     | Client  |                                  |     Server    |
     |         |<--(B)---- Access Token ---------<|               |
     |         |                                  |               |
     +---------+                                  +---------------+
```

# Issuing an Access Token

## Success response

If the access token request is valid, authorization server issues the access token and optional refresh token.

* access_token: Required.
* token_type: Required. The type of token
  issued. [Bearer](https://datatracker.ietf.org/doc/html/rfc6750), [Mac](https://datatracker.ietf.org/doc/html/rfc6749#ref-OAuth-HTTP-MAC)
  or any other scheme for oauth (e.g. saml / threadmodel / wrap).
* expires_in: Recommended. The lifetime in seconds of the access token.
* refresh_token: Optional. The refresh token, which can be used to obtain new access tokens using the same
  authorization grant.
* scope: Optional if identical to the scope requested by the client; otherwise, REQUIRED. The scope of the
  access token.

The parameters are included in the http response body using the `application/json` media type. The authorization
server must include the HTTP `Cache-Control: no-store` and `Pragma: no-cache` header. The client must ignore
unrecognized value names in the response. The sizes of tokens is undefined, so client should avoid making
assumptions about value sizes. The authorization server should document the size of any value it issues.

## Error response

The authorization server respond with an HTTP 400 and includes the following parameters with the response.
Values for the error response parameter must not include characters outside the set %x20-21 / %x23-5B / %x5D-7E.

* error: REQUIRED. The error code of the request. be `invalid_request`, `invalid_client`, `invalid_grant`,
  `unauthorized_client`, `unsupported_grant_type` or `invalid_scope`.
* error_description: Optional. Human-readable text providing addtional information.
* error_uri: Optional. A URI identifying a human-readable web page with information about the error, used to
  provide the client developer with additional information about the error.

# Refreshing an Access Token

If the authorization server issued a refresh token to the client, the client makes a refresh request to the
token endpoint by adding the following parameters using the "application/x-www-form-urlencoded". Please follow
Success or error response described as above.

* grant_type: Required. Value must be set to `refresh_token`.
* refresh_token: Required. The refresh token issued to the client.
* scope: Optional. The requested scope must not include any scope not originally granted by the resource owner,
  and if omitted is treated as equal to the scope originally granted by the resource owner.

Because refresh tokens are typically long-lasting credentials used to request additional access tokens, the
refresh token is bound to the client to which it was issued. If the client type is confidential or the client
was issued client credentials (or assigned other authentication requirements), the client must authenticate with
the authorization server.
The authorization server must

* require client authentication for confidential clients or for any client that was issued client credentials (
  or with other authentication requirements).
* authenticate the client if client authentication is included and ensure that the refresh token was issued to
  the authenticated client.
* validate the refresh token.

The authorization server may issue a new refresh token, in which case the client must discard the old refresh
token and replace it with the new refresh token. The authorization server may revoke the old refresh token after
issuing a new refresh token to the client. If a new refresh token is issued, the refresh token scope must be
identical to that of the refresh token included by the client in the request.

# OIDC

## Relation between OAuth (OAuth 2.0) and OIDC (OpenID Connect)

OIDC runs on OAuth, which means when the Client request to get authorization grant with the `open_id` scope and
Authorization server authenticate with scope with oauth authorization flow. Finally, ID Token is issued with
access token.

## What is the purpose of OIDC?

OIDC is used for **authentication** of the Resource Owner. With ID Token, Client can identify and authenticate
end-user as well as delegate login to OpenID Provider to enter Partner's endpoint in a secure manner.

# Bearer Token

Bearer token is used for client authentication on OAuth framework. This one way to grant access resource by the
resource owner. OAuth framework does not require additional credential / authentication with bearer token
authentication. It means the authentication server need to build their own authentication or access control by
themselves. Therefore, some of the security considerations are recommended to mitigate threats.

1. Safeguard bearer token implementation by the server and client
2. Validate TLS certificate chains and always use TLS (https)
3. Don't store bearer tokens in cookies
4. Issue short-lived and scoped bearer tokens

There are three ways to create authenticated request. Please note that Client MUST NOT use more than one method
to transmit the token in each request.

1. Authorization Request Header
   ```http request
   GET /resource HTTP/1.1
   Host: lia.archive.com
   Authorization: Bearer mF_9.B5f-4.1JqM
   ```
2. Form-encoded body parameter
   ```http request
   POST /resource HTTP/1.1
   Host: lia.archive.com
   Content-Type: application/x-www-form-urlencoded
   
   access_token=mF_9.B5f-4.1JqM
   ```
3. URI Query parameter
   ```http request
   GET /resource?access_token=mF_9.B5f-4.1JqM HTTP/1.1
   Host: lia.archive.com
   ```
