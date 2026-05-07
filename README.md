# WireMock Standalone - Finacle API Mock Server

This project uses **WireMock Standalone** to mock the Finacle APIs from your Postman collection. This is a simpler, more straightforward approach that doesn't require building a Java application.

## Project Structure

```
HelloWorld/
├── mappings/                                    # WireMock mock endpoint definitions
│   ├── 01-auth.json                           # Authentication endpoint
│   ├── 02-statement-with-token.json           # Statement with valid token
│   ├── 03-statement-no-token.json             # Statement without token (401)
│   ├── 04-invalid-credentials.json            # Invalid credentials scenario
│   ├── 05-account-not-found.json              # Account not found (404)
│   └── 06-server-error.json                   # Server error scenario (500)
├── __files/                                     # Static response files (if needed)
├── wiremock-standalone-3.3.1.jar              # WireMock standalone executable
├── test.sh                                      # Test script with curl examples
├── setup-wiremock.sh                           # Setup script to download WireMock
└── FinacleStatement.postman_collection.json   # Original Postman collection
```

## Prerequisites

- Java 11+
- Bash shell
- curl (for testing)

## Quick Start

### Step 1: Download WireMock Standalone

```bash
cd /home/pramodgautam/IdeaProjects/HelloWorld
chmod +x setup-wiremock.sh
./setup-wiremock.sh
```

Or download manually:
```bash
curl -o wiremock-standalone-3.3.1.jar https://repo1.maven.org/maven2/org/wiremock/wiremock-standalone/3.3.1/wiremock-standalone-3.3.1.jar
```

### Step 2: Start WireMock Server

```bash
java -jar wiremock-standalone-3.3.1.jar --port 8080
```

You should see output like:
```
2026-05-07 10:00:00 WireMock started on http://localhost:8080
```

### Step 3: Test the APIs

In another terminal:
```bash
chmod +x test.sh
./test.sh
```

## Available Mock Endpoints

### 1. Authentication Endpoint

**Endpoint:** `POST /api/Auth`

**Success Request:**
```bash
curl -X POST http://localhost:8080/api/Auth \
  -H "Content-Type: application/json" \
  -d '{
    "UserName": "NCHL",
    "Password": "TEST"
  }'
```

**Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "Status": true,
  "message": "Success"
}
```

**Invalid Credentials Request:**
```bash
curl -X POST http://localhost:8080/api/Auth \
  -H "Content-Type: application/json" \
  -d '{
    "UserName": "INVALID",
    "Password": "WRONG"
  }'
```

**Error Response (401 Unauthorized):**
```json
{
  "Status": false,
  "message": "Invalid username or password"
}
```

### 2. Corporate Pay Statement Endpoint

**Endpoint:** `POST /api/v1/CorporatePay/GetCorporatePayStatement`

**Required Header:** `Authorization: Bearer <token>`

**Success Request:**
```bash
curl -X POST http://localhost:8080/api/v1/CorporatePay/GetCorporatePayStatement \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "MerchantId": "NCHL",
    "AccountNumber": "99000111165000011",
    "StartDate": "2024-01-01",
    "EndDate": "2025-01-04"
  }'
```

**Success Response (200 OK):**
```json
{
  "Status": true,
  "message": "Success",
  "MerchantId": "NCHL",
  "AccountNumber": "99000111165000011",
  "StartDate": "2024-01-01",
  "EndDate": "2025-01-04",
  "Transactions": [
    {
      "TransactionId": "TXN001",
      "Date": "2024-01-15",
      "Description": "Payment received",
      "Amount": 5000.00,
      "Type": "Credit"
    },
    {
      "TransactionId": "TXN002",
      "Date": "2024-01-20",
      "Description": "Transfer out",
      "Amount": 2000.00,
      "Type": "Debit"
    }
  ]
}
```

**Without Authorization Header:**
```bash
curl -X POST http://localhost:8080/api/v1/CorporatePay/GetCorporatePayStatement \
  -H "Content-Type: application/json" \
  -d '{...}'
```

**Response (401 Unauthorized):**
```json
{
  "Status": false,
  "message": "Unauthorized - Token required"
}
```

### 3. Special Test Cases

#### Account Not Found
Request with account number `00000000000000000`:
```bash
curl -X POST http://localhost:8080/api/v1/CorporatePay/GetCorporatePayStatement \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "AccountNumber": "00000000000000000",
    "MerchantId": "NCHL",
    "StartDate": "2024-01-01",
    "EndDate": "2025-01-04"
  }'
```

**Response (404 Not Found):**
```json
{
  "Status": false,
  "message": "Account not found"
}
```

#### Server Error
Request with account number `99999999999999999`:
```bash
curl -X POST http://localhost:8080/api/v1/CorporatePay/GetCorporatePayStatement \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "AccountNumber": "99999999999999999",
    "MerchantId": "NCHL",
    "StartDate": "2024-01-01",
    "EndDate": "2025-01-04"
  }'
```

**Response (500 Internal Server Error):**
```json
{
  "Status": false,
  "message": "Internal server error"
}
```

## WireMock Admin API

### View All Configured Stubs
```bash
curl http://localhost:8080/__admin/mappings | jq
```

### View Request Journal (All requests received)
```bash
curl http://localhost:8080/__admin/requests | jq
```

### View Specific Request History
```bash
curl "http://localhost:8080/__admin/requests?limit=10" | jq
```

### Reset All Stubs
```bash
curl -X POST http://localhost:8080/__admin/reset
```

### Add a New Stub Dynamically
```bash
curl -X POST http://localhost:8080/__admin/mappings \
  -H "Content-Type: application/json" \
  -d '{
    "request": {
      "method": "GET",
      "url": "/api/custom"
    },
    "response": {
      "status": 200,
      "jsonBody": {
        "message": "Custom response"
      }
    }
  }'
```

## Modifying Mock Responses

### Edit Mapping Files

Each mapping is in the `mappings/` directory:

1. **01-auth.json** - Edit token or response
2. **02-statement-with-token.json** - Edit transaction data
3. **03-statement-no-token.json** - Edit error messages
4. **04-invalid-credentials.json** - Edit auth failure response
5. **05-account-not-found.json** - Edit 404 response
6. **06-server-error.json** - Edit 500 response

### Add New Mapping

Create a new file in the `mappings/` directory:

```json
{
  "id": "my-custom-endpoint",
  "request": {
    "method": "POST",
    "url": "/api/custom"
  },
  "response": {
    "status": 200,
    "headers": {
      "Content-Type": "application/json"
    },
    "jsonBody": {
      "message": "Custom response"
    }
  }
}
```

Then reload WireMock (or add it dynamically via admin API).

## Using with Postman

1. Import your existing `FinacleStatement.postman_collection.json` into Postman
2. Update the base URL:
   - Change from: `https://finaclestatementService.nmbbank.com.np`
   - Change to: `http://localhost:8080`
3. Run the requests against the mock server

## Advanced Features

### Response Templating

Add dynamic responses based on request data:

```json
{
  "request": {
    "method": "POST",
    "url": "/api/echo"
  },
  "response": {
    "status": 200,
    "jsonBody": {
      "receivedMerchant": "{{jsonPath request.body '$.MerchantId'}}"
    }
  }
}
```

### Request Body Matching

Match specific JSON paths:

```json
{
  "request": {
    "method": "POST",
    "url": "/api/test",
    "bodyPatterns": [
      {
        "matchesJsonPath": "$.name",
        "equalTo": "John"
      }
    ]
  }
}
```

### Delay Responses

Add latency to simulate real network conditions:

```json
{
  "response": {
    "status": 200,
    "jsonBody": {...},
    "fixedDelayMilliseconds": 2000
  }
}
```

## Troubleshooting

### Port 8080 Already in Use
```bash
java -jar wiremock-standalone-3.3.1.jar --port 9090
```

### Check if WireMock is Running
```bash
curl -s http://localhost:8080/__admin/mappings | jq '.[] | .id'
```

### View WireMock Logs
Start with verbose logging:
```bash
java -jar wiremock-standalone-3.3.1.jar --port 8080 --verbose
```

## Documentation

- [WireMock Official Site](https://wiremock.org/)
- [WireMock Standalone Quickstart](https://wiremock.org/docs/standalone)
- [Request Matching Guide](https://wiremock.org/docs/request-matching/)
- [Response Templating](https://wiremock.org/docs/response-templating/)

## Tips

1. **Reload Mappings** - Stop and restart WireMock to reload mappings from files
2. **Hot Reload** - Use admin API to add/update stubs without restarting
3. **Debugging** - Check `__admin/requests` to see what requests WireMock received
4. **Priority** - Use `priority` field in mappings for more specific matches (higher priority = matched first)

## License

This setup is provided for development and testing purposes.


