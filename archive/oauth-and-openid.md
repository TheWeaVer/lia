# Introduction

This article summarize OAuth 2.0 with Bearer Token ([RFC 6749](https://datatracker.ietf.org/doc/html/rfc6749) /
[RFC 6750](https://datatracker.ietf.org/doc/html/rfc6750)) and [OpenID Connect](https://openid.net/). 


# OAuth 2.0

OAuth 2.0 is **authorization** framework to give accessibility to client with access token. Access token has a
specific scope, lifetime, and other access attributes. The interaction between the authorization server and
resource server is beyond the scope of OAuth.

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

(A)  The client requests authorization from the resource owner.  The
authorization request can be made directly to the resource owner
(as shown), or preferably indirectly via the authorization
server as an intermediary.

(B)  The client receives an authorization grant, which is a
credential representing the resource owner's authorization,
expressed using one of four grant types defined in this
specification or using an extension grant type.  The
authorization grant type depends on the method used by the
client to request authorization and the types supported by the
authorization server.

(C)  The client requests an access token by authenticating with the
authorization server and presenting the authorization grant.

(D)  The authorization server authenticates the client and validates
the authorization grant, and if valid, issues an access token.

(E)  The client requests the protected resource from the resource
server and authenticates by presenting the access token.

(F)  The resource server validates the access token, and if valid,
serves the request.

With this process
* Client doesn't need to request with Resource Owner's credential.
* Resource Server doesn't need to authenticate user always.

# Obtaining Authorization

## Authorization Code Grant

This grant type is used to obtain access token and refresh token. It's important to create redirection-based flow
with confidential client.

* Resource owner: Target to grant to access resource to the client
* User-agent: User agent is a browser or mobile application via which the resource owner communicates to the
  authorization server.
* Client: Client is the application code that wants to access the resources of the user on the resource server.
  Client can be a web browser, native client or web server. If the client is on the server, it's considered a
  confidential client. If not it's public client.


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


(A) User-agent initiates the flow with their client identifier by redirecting resource owner's user-agent to
authorization server's endpoint.

(B) The authorization server authenticates the resource owner and establishes whether the resource owner grants
or denies the client's authorization request.

(C) Assuming that resource owner grants the client, authorization server redirects the user-agent to the client with
authorization code.

(D) Client requests an access token with the given code and used redirect uri in previous step. The authorization
server verifies and authenticates the made request.
   * The authorization server authenticates the client by matching the URI used to redirect the client in
     step (C).
   * It is compared whether the client acquiring authorization code and the client acquiring access token are the same. 

(E) If the (D) is valid, authorization server respond back with an access token and, optionally, refresh token.
   * When the client tries to get access token with refresh token, the authorization server does not request
     this authorization code grant.


## Implicit Grant

It's used for the client which can not manage credential such as single page application. It's grant without
code and give a authorization. So, authorization must have short lifetime and authorization server must not
returns refresh token. This method is risky way so, OAuth 2.1 introduces PKCE.


### OAuth 2.1 does not recommend Implicit Grant

OAuth 2.0 supports [Implicit Grant](https://datatracker.ietf.org/doc/html/rfc6749#section-1.3.2), however, OAuth 2.1 doesn't recommend it but recommend trying 
authorization request with [PKCE](https://datatracker.ietf.org/doc/html/draft-ietf-oauth-v2-1-01#oauth-2-0-differences). Moreover, there are other considerations in OAuth flow.  OAuth 2.1
already expired, however, those approached are problem to think about when providing OAuth flow.


## Resource Owner Password Credentials Grant

TBD


## Client Credentials Grant

TBD


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

There are three recommended ways to request authentication
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
   Host: server.example.com
   ```
