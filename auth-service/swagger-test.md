# Auth Service Swagger Test Guide

Link Swagger UI: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)

## 1. Register User
**Endpoint:** `POST /api/v1/auth/register`

**JSON Payload:**
```json
{
  "username" : "john.doe",
  "firstname": "John",
  "lastname": "Doe",
  "email": "john.doe@example.com",
  "password": "password123",
  "role": "USER"
}
```

## 2. Login
**Endpoint:** `POST /api/v1/auth/login`

**JSON Payload:**
```json
{
  "email": "john.doe@example.com",
  "password": "password123"
}
```
*Note: Copy the `access_token` from the response to use in other services if required.*
