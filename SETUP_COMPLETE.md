# WireMock Finacle API Setup - Complete Summary

## ✅ Setup Complete!

Your WireMock mock API server is now ready to use. Here's what has been created:

## 📁 Project Structure

```
HelloWorld/
├── 📄 README.md                          # Comprehensive documentation
├── 📄 QUICKSTART.md                      # Quick start guide
├── 📄 setup-wiremock.sh                  # Download script
├── 📄 test.sh                            # Test script with curl examples
│
├── 📁 mappings/                          # Mock endpoint definitions
│   ├── 01-auth.json                      # Auth endpoint (POST /api/Auth)
│   ├── 02-statement-with-token.json      # Statement with valid token
│   ├── 03-statement-no-token.json        # 401 Unauthorized response
│   ├── 04-invalid-credentials.json       # Invalid login scenario
│   ├── 05-account-not-found.json         # 404 Not Found scenario
│   └── 06-server-error.json              # 500 Server Error scenario
│
└── 📁 __files/                           # Static files (created, ready for use)
```

## 🚀 Getting Started in 3 Steps

### Step 1: Download WireMock Standalone

```bash
cd /home/pramodgautam/IdeaProjects/HelloWorld
./setup-wiremock.sh
```

Or manually:
```bash
curl -o wiremock-standalone-3.3.1.jar \
  https://repo1.maven.org/maven2/org/wiremock/wiremock-standalone/3.3.1/wiremock-standalone-3.3.1.jar
```

### Step 2: Start the Mock Server

```bash
java -jar wiremock-standalone-3.3.1.jar --port 8080
```

✅ Server starts on: `http://localhost:8080`

### Step 3: Test the APIs

```bash
# In another terminal
./test.sh
```

Or test manually:
```bash
# Get token
curl -X POST http://localhost:8080/api/Auth \
  -H "Content-Type: application/json" \
  -d '{"UserName":"NCHL","Password":"TEST"}' | jq

# Get statement (use token from above)
curl -X POST http://localhost:8080/api/v1/CorporatePay/GetCorporatePayStatement \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <INSERT_TOKEN_HERE>" \
  -d '{"MerchantId":"NCHL","AccountNumber":"99000111165000011","StartDate":"2024-01-01","EndDate":"2025-01-04"}' | jq
```

## 📋 Mock API Endpoints

### 1. **Authentication** `POST /api/Auth`
- **Status Code:** `200 OK`
- **Response:** Returns JWT token
- **Test with:** `curl test.sh` or see QUICKSTART.md

### 2. **Get Corporate Pay Statement** `POST /api/v1/CorporatePay/GetCorporatePayStatement`
- **Status Code:** `200 OK` (with valid token)
- **Status Code:** `401 Unauthorized` (without token)
- **Response:** Transaction history with sample data

### 3. **Test Scenarios Configured**
- ✅ Valid authentication
- ❌ Invalid credentials (401)
- ❌ Missing token (401)
- ❌ Account not found (404)
- ❌ Server error (500)

## 🎯 Key Features

### ✨ Pre-configured Mock Endpoints
- 6 different scenario mappings ready to use
- Real-world test cases (success, auth errors, not found, server error)
- Proper HTTP status codes and error messages

### 🔧 Easy Customization
- Edit `.json` files in `mappings/` directory
- Add new endpoints by creating new mapping files
- Use response templating for dynamic responses

### 📊 Admin Console
- View all configured stubs: `http://localhost:8080/__admin/mappings`
- Check request history: `http://localhost:8080/__admin/requests`
- Add stubs dynamically via API
- Reset all stubs: `curl -X POST http://localhost:8080/__admin/reset`

### 🧪 Testing Tools Included
- `test.sh` - Comprehensive test suite with curl
- QUICKSTART.md - Ready-to-use command examples
- README.md - Full documentation

## 📖 Documentation Files

| File | Purpose |
|------|---------|
| **QUICKSTART.md** | Get started in minutes - copy/paste examples |
| **README.md** | Complete reference guide with all options |
| **test.sh** | Automated test script - run all scenarios |
| **setup-wiremock.sh** | Download WireMock standalone JAR |

## 🔌 Use with Postman

1. Open `FinacleStatement.postman_collection.json` in Postman
2. Set collection variable or base URL to: `http://localhost:8080`
3. Run requests - they'll hit your mock server!

## 💡 Next Steps

### Quick Tests
```bash
./test.sh                              # Run all tests
cat QUICKSTART.md                      # See copy/paste examples
```

### Customize Responses
```bash
# Edit any mapping file
nano mappings/02-statement-with-token.json

# Restart WireMock to reload
# (or use admin API for hot reload)
```

### Add More Mocks
```bash
# Create new mapping file
cat > mappings/07-custom-endpoint.json << 'EOF'
{
  "id": "my-endpoint",
  "request": {"method": "GET", "url": "/api/custom"},
  "response": {"status": 200, "jsonBody": {"message": "Hello"}}
}
EOF
```

### Integration Testing
```bash
# Point your Java/Python/Node app to http://localhost:8080
# All requests will be intercepted by WireMock
# Mock responses will be returned consistently
```

## ⚙️ Configuration Options

```bash
# Different port
java -jar wiremock-standalone-3.3.1.jar --port 9090

# Verbose logging
java -jar wiremock-standalone-3.3.1.jar --port 8080 --verbose

# Reset mappings on startup
java -jar wiremock-standalone-3.3.1.jar --port 8080 --mappings-reset-on-startup

# Custom mappings directory
java -jar wiremock-standalone-3.3.1.jar --port 8080 --mappings /path/to/mappings
```

## 🐛 Troubleshooting

```bash
# Check if WireMock is running
curl http://localhost:8080/__admin/mappings | jq

# View request history
curl http://localhost:8080/__admin/requests | jq '.requests[] | {method, url, status}'

# Reset all configurations
curl -X POST http://localhost:8080/__admin/reset

# Check mapped endpoints
curl http://localhost:8080/__admin/mappings | jq '.[] | {id, request}'
```

## 📚 Resources

- **WireMock Docs:** https://wiremock.org/
- **Request Matching:** https://wiremock.org/docs/request-matching/
- **Response Templating:** https://wiremock.org/docs/response-templating/
- **Admin API:** https://wiremock.org/docs/running-standalone/

## ✨ Status

✅ **Ready to Use!**

Everything is set up and ready to go. Download WireMock, start the server, and begin testing!

Questions? Check QUICKSTART.md or README.md for detailed examples and explanations.

---

**Quick Command Reference:**

```bash
# Setup
./setup-wiremock.sh

# Start server
java -jar wiremock-standalone-3.3.1.jar --port 8080

# Test (in another terminal)
./test.sh

# View configured stubs
curl http://localhost:8080/__admin/mappings | jq

# View requests received
curl http://localhost:8080/__admin/requests | jq
```

Enjoy! 🎉

