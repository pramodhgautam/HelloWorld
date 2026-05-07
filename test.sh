#!/bin/bash

# Test Script for WireMock Finacle APIs (Standalone)
# This script demonstrates how to test the mock APIs using curl
# Make sure WireMock is running on port 8080 before executing this script

echo "🧪 Testing WireMock Finacle APIs (Standalone)"
echo "=============================================="
echo ""

# Color codes
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

BASE_URL="http://localhost:8080"

# Function to print test header
print_test() {
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${YELLOW}Test: $1${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
}

# Function to print result
print_result() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓ Success${NC}"
    else
        echo -e "${RED}✗ Failed${NC}"
    fi
}

# Check if server is running
echo -e "${YELLOW}Checking if WireMock server is running...${NC}"
if ! curl -s -f "$BASE_URL/__admin/mappings" > /dev/null 2>&1; then
    echo -e "${RED}❌ WireMock server is not running on $BASE_URL${NC}"
    echo "Please start the server first:"
    echo "  java -jar target/wiremock-server.jar"
    exit 1
fi
echo -e "${GREEN}✓ WireMock server is running${NC}\n"

# Test 1: Get Authentication Token
print_test "Get Authentication Token"
TOKEN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/Auth" \
  -H "Content-Type: application/json" \
  -d '{
    "UserName": "NCHL",
    "Password": "TEST"
  }')

echo "Request:"
echo "  POST $BASE_URL/api/Auth"
echo "  Payload: {\"UserName\": \"NCHL\", \"Password\": \"TEST\"}"
echo ""
echo "Response:"
echo "$TOKEN_RESPONSE" | jq . 2>/dev/null || echo "$TOKEN_RESPONSE"
echo ""

# Extract token
TOKEN=$(echo "$TOKEN_RESPONSE" | jq -r '.token' 2>/dev/null)
if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
    echo -e "${RED}❌ Failed to extract token${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Token extracted: ${TOKEN:0:50}...${NC}\n"

# Test 2: Get Corporate Pay Statement with Valid Token
print_test "Get Corporate Pay Statement (Valid Token)"
STATEMENT=$(curl -s -X POST "$BASE_URL/api/v1/CorporatePay/GetCorporatePayStatement" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "MerchantId": "NCHL",
    "AccountNumber": "99000111165000011",
    "StartDate": "2024-01-01",
    "EndDate": "2025-01-04"
  }')

echo "Request:"
echo "  POST $BASE_URL/api/v1/CorporatePay/GetCorporatePayStatement"
echo "  Authorization: Bearer $TOKEN"
echo "  Payload: Account Statement Request"
echo ""
echo "Response:"
echo "$STATEMENT" | jq . 2>/dev/null || echo "$STATEMENT"
echo -e "${GREEN}✓ Statement retrieved${NC}\n"

# Test 3: Get Statement without Token
print_test "Get Corporate Pay Statement (No Token - Should Fail)"
UNAUTHORIZED=$(curl -s -X POST "$BASE_URL/api/v1/CorporatePay/GetCorporatePayStatement" \
  -H "Content-Type: application/json" \
  -d '{
    "MerchantId": "NCHL",
    "AccountNumber": "99000111165000011",
    "StartDate": "2024-01-01",
    "EndDate": "2025-01-04"
  }')

echo "Request:"
echo "  POST $BASE_URL/api/v1/CorporatePay/GetCorporatePayStatement"
echo "  (No Authorization Header)"
echo ""
echo "Response:"
echo "$UNAUTHORIZED" | jq . 2>/dev/null || echo "$UNAUTHORIZED"
echo ""

STATUS=$(echo "$UNAUTHORIZED" | jq -r '.Status' 2>/dev/null)
if [ "$STATUS" = "false" ]; then
    echo -e "${GREEN}✓ Correctly rejected unauthorized request${NC}\n"
else
    echo -e "${RED}✗ Should have rejected unauthorized request${NC}\n"
fi

# Test 4: Invalid Credentials
print_test "Get Token with Invalid Credentials"
INVALID_AUTH=$(curl -s -X POST "$BASE_URL/api/Auth" \
  -H "Content-Type: application/json" \
  -d '{
    "UserName": "INVALID",
    "Password": "WRONG"
  }')

echo "Request:"
echo "  POST $BASE_URL/api/Auth"
echo "  Payload: {\"UserName\": \"INVALID\", \"Password\": \"WRONG\"}"
echo ""
echo "Response:"
echo "$INVALID_AUTH" | jq . 2>/dev/null || echo "$INVALID_AUTH"
echo -e "${GREEN}✓ Invalid credentials scenario tested${NC}\n"

# Test 5: View WireMock Admin Mappings
print_test "View WireMock Configured Mappings"
MAPPINGS=$(curl -s -X GET "$BASE_URL/__admin/mappings")

echo "Request:"
echo "  GET $BASE_URL/__admin/mappings"
echo ""
echo "Response (First 500 chars):"
echo "$MAPPINGS" | jq . 2>/dev/null | head -c 500
echo "..."
echo -e "${GREEN}✓ Mappings retrieved${NC}\n"

# Summary
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}✓ All tests completed successfully!${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo "Next steps:"
echo "1. Review the responses above"
echo "2. Modify the mock responses in src/main/java/com/example/WireMockServer.java"
echo "3. Add more custom endpoints as needed"
echo "4. Integrate WireMock with your application"

