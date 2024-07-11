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


## OAuth 2.1 does not recommend Implicit Grant

OAuth 2.0 supports [Implicit Grant](https://datatracker.ietf.org/doc/html/rfc6749#section-1.3.2), however,
OAuth 2.1 doesn't recommend it but recommend trying authorization request with
[PKCE](https://datatracker.ietf.org/doc/html/draft-ietf-oauth-v2-1-01#oauth-2-0-differences). Moreoever, 
there are other considerations in OAuth flow.  OAuth 2.1 already expired, however, those approached are problem 
to think about when providing OAuth flow.


# Bearer Token

TBD


# OIDC

## Relation between OAuth (OAuth 2.0) and OIDC (OpenID Connect)

OIDC runs on OAuth, which means when the Client request to get authorization grant with the `open_id` scope and 
Authorization server authenticate with scope with oauth authorization flow. Finally, ID Token is issued with
access token. 


## What is the purpose of OIDC?

OIDC is used for **authentication** of the Resource Owner. With ID Token, Client can identify and authenticate
end-user as well as delegate login to OpenID Provider to enter Partner's endpoint in a secure manner. 
