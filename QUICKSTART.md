# Quick Start Guide - WireMock Standalone

## 1. Download WireMock

```bash
cd /home/pramodgautam/IdeaProjects/HelloWorld

# Option A: Using the setup script
chmod +x setup-wiremock.sh
./setup-wiremock.sh

# Option B: Manual download
curl -o wiremock-standalone-3.3.1.jar \
  https://repo1.maven.org/maven2/org/wiremock/wiremock-standalone/3.3.1/wiremock-standalone-3.3.1.jar
```

## 2. Start WireMock Server

```bash
java -jar wiremock-standalone-3.3.1.jar --port 8080
```

You should see:
```
2026-05-07 10:00:00 WireMock started on http://localhost:8080
```

## 3. Test in Another Terminal

### Get Authentication Token
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/Auth \
  -H "Content-Type: application/json" \
  -d '{"UserName":"NCHL","Password":"TEST"}' | jq -r '.token')

echo "Token: $TOKEN"
```

### Get Corporate Pay Statement
```bash
curl -X POST http://localhost:8080/api/v1/CorporatePay/GetCorporatePayStatement \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "MerchantId": "NCHL",
    "AccountNumber": "99000111165000011",
    "StartDate": "2024-01-01",
    "EndDate": "2025-01-04"
  }' | jq
```

### Run Full Test Suite
```bash
chmod +x test.sh
./test.sh
```

## 4. View Mock Endpoints

```bash
# List all configured stubs
curl http://localhost:8080/__admin/mappings | jq '.[] | {id, request}'

# View all requests received
curl http://localhost:8080/__admin/requests | jq
```

## 5. Test Invalid Scenarios

### Invalid Credentials
```bash
curl -s -X POST http://localhost:8080/api/Auth \
  -H "Content-Type: application/json" \
  -d '{"UserName":"INVALID","Password":"WRONG"}' | jq
```

### Missing Authorization Token
```bash
curl -s -X POST http://localhost:8080/api/v1/CorporatePay/GetCorporatePayStatement \
  -H "Content-Type: application/json" \
  -d '{"MerchantId":"NCHL","AccountNumber":"99000111165000011","StartDate":"2024-01-01","EndDate":"2025-01-04"}' | jq
```

### Account Not Found
```bash
curl -s -X POST http://localhost:8080/api/v1/CorporatePay/GetCorporatePayStatement \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"MerchantId":"NCHL","AccountNumber":"00000000000000000","StartDate":"2024-01-01","EndDate":"2025-01-04"}' | jq
```

### Server Error
```bash
curl -s -X POST http://localhost:8080/api/v1/CorporatePay/GetCorporatePayStatement \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"MerchantId":"NCHL","AccountNumber":"99999999999999999","StartDate":"2024-01-01","EndDate":"2025-01-04"}' | jq
```

## 6. Customize Mock Responses

### Edit Mapping Files
All mock definitions are in the `mappings/` directory:
- `01-auth.json` - Authentication responses
- `02-statement-with-token.json` - Successful statement responses
- `03-statement-no-token.json` - 401 Unauthorized responses
- `04-invalid-credentials.json` - Invalid login responses
- `05-account-not-found.json` - 404 Account not found
- `06-server-error.json` - 500 Server error

After editing, restart WireMock to load the changes.

### Add New Mock Dynamically
```bash
curl -X POST http://localhost:8080/__admin/mappings \
  -H "Content-Type: application/json" \
  -d '{
    "id": "my-new-endpoint",
    "request": {
      "method": "POST",
      "url": "/api/custom"
    },
    "response": {
      "status": 200,
      "jsonBody": {"message": "Hello from custom endpoint"}
    }
  }'
```

## 7. Use with Postman

1. Open your `FinacleStatement.postman_collection.json` in Postman
2. Edit collection variables or set base URL to `http://localhost:8080`
3. Run requests against the mock server

## 8. Stop WireMock

Press `Ctrl+C` in the terminal where WireMock is running.

## Useful Commands

```bash
# Change port
java -jar wiremock-standalone-3.3.1.jar --port 9090

# Enable verbose logging
java -jar wiremock-standalone-3.3.1.jar --port 8080 --verbose

# View request history with filtering
curl "http://localhost:8080/__admin/requests?limit=5" | jq '.requests[] | {method, url}'

# Reset all stubs
curl -X POST http://localhost:8080/__admin/reset

# Test connectivity
curl -v http://localhost:8080/__admin/mappings
```

## Next Steps

- Explore `README.md` for detailed documentation
- Add more custom mappings in the `mappings/` directory
- Use response templating for dynamic responses
- Integrate with your application
- Run automated tests against the mock server

Happy mocking! 🚀

