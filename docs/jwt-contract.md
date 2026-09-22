# JWT Contract — Shared Across All RideLink Services

This is the shared contract for JWT tokens issued by Account Service (Member 1) 
and validated by Driver, Ride, and Payment services. Any change here must be 
agreed by the whole group before implementation.

## 1. Secret Key
- Shared via the `JWT_SECRET` environment variable — identical value across all four services
- Never committed to the repository in any form
- The actual value is shared privately between team members (chat/shared doc), not in this file

## 2. Role Claim
- Claim name: `role`
- Value: plain enum name — `PASSENGER`, `DRIVER`, or `ADMIN` (no `ROLE_` prefix)
- Note: the `ROLE_` prefix is a Spring Security convention added *inside application code* 
  when building authorities for `.hasRole()` checks — it is not part of the stored claim

## 3. User Identifier
- The `sub` (subject) claim holds the account's **email**, not a numeric ID
- If a service needs the numeric account ID, raise this with the group — 
  it is not currently included as a claim

## 4. Token Expiry
- 1 hour (3,600,000 ms) from issue time

## 5. Library and Algorithm
- Library: JJWT (`io.jsonwebtoken`), version `0.12.6`
- Algorithm: HMAC-SHA, resolved automatically by `Keys.hmacShaKeyFor()` based on secret 
  key length — confirm which HS variant (HS256/HS384/HS512) results from the shared 
  secret before assuming HS256 in a validating service

## Example token payload
\`\`\`json
{
  "sub": "passenger10@example.com",
  "role": "PASSENGER",
  "iat": 1790053912,
  "exp": 1790057512
}
\`\`\`