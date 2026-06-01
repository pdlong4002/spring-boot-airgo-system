# API Gateway Rate Limiting Test Guide

This guide helps you verify that the Rate Limiting (Token Bucket algorithm) is working correctly on the API Gateway.

## 1. Configured Limits
The following limits are applied in `GatewayConfig.java`:

| Service | Route Path | Replenish Rate | Burst Capacity |
| :--- | :--- | :--- | :--- |
| **Auth Service** | `/api/v1/auth/**` | 10 req/s | 20 |
| **Flight Service** | `/api/v1/flights/**` | 5 req/s | 10 |
| **Booking Service** | `/api/v1/bookings/**` | 2 req/s | 5 |

- **Replenish Rate**: How many tokens are added to the bucket every second.
- **Burst Capacity**: The maximum number of requests a user can make in a single second (if they have enough tokens).

## 2. How to Test

### Manual Test (Swagger)
1. Go to the API Gateway port: [http://localhost:8080](http://localhost:8080) (or via the individual service Swaggers redirected through the gateway).
2. Choose an endpoint, for example: `GET http://localhost:8080/api/v1/bookings/{code}`.
3. Click the "Execute" button **rapidly** multiple times (more than 5 times within 1-2 seconds for Booking Service).
4. **Expected Result**: 
   - The first few requests will succeed (200 OK).
   - After exceeding the limit, you will receive a **429 Too Many Requests** error.

### Automation Test (cURL / PowerShell)
You can run a loop to flood the gateway:

**Using PowerShell:**
```powershell
# Test Booking Service (Limit: 2 req/s, Burst: 5)
for ($i=1; $i -le 10; $i++) {
    Invoke-RestMethod -Uri "http://localhost:8080/api/v1/bookings/TEST" -Method Get -ErrorAction SilentlyContinue
    Write-Host "Request $i sent"
}
```

**Using cURL (Bash):**
```bash
# Test Booking Service
for i in {1..10}; do 
  curl -i http://localhost:8080/api/v1/bookings/TEST; 
done
```

## 3. Troubleshooting
- **Ensure Redis is running**: Rate limiting requires Redis. Check with `docker ps` to see if the `redis` container is Up.
- **Check Logs**: If you see `429` errors even with slow requests, check the `replenishRate` in `GatewayConfig.java`.
- **IP Recognition**: The current `userKeyResolver` uses the remote IP address. If testing from the same machine, all requests share the same bucket.
