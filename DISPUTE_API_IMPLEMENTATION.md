# Dispute API Implementation Summary

## Overview
This document outlines the implementation of a "Raise Dispute" API endpoint for the Finacle Statement application. The implementation allows users to post disputes for specific transactions identified by their `TranId`.

## Changes Made

### 1. **Created WireMock Mapping for Dispute Endpoint**
   - **File**: `/mappings/07-raise-dispute.json`
   - **Endpoint**: `POST /api/v1/CorporatePay/RaiseDispute`
   - **Features**:
     - Requires valid Bearer token in Authorization header
     - Accepts JSON body with transaction and dispute details
     - Returns 200 OK with dispute ID and reference number
     - Response includes DisputeId, TranId, DisputeDate, DisputeReason, Remarks, Status, Amount, and ReferenceNumber

### 2. **Updated WireMock Server Configuration**
   - **File**: `/src/main/java/com/example/FinacleWireMockServer.java`
   - **Changes**:
     - Added Mock 4: Raise Dispute Endpoint (with valid token) - returns 200 OK
     - Added endpoint logging to show available endpoints
     - Added `generateDisputeResponse()` method to create dispute response payloads
     - Fixed package imports for WireMock 3.3.1 (using `com.github.tomakehurst.wiremock`)

### 3. **Added API Client Method for Dispute**
   - **File**: `/src/main/java/com/example/FinacleAPIClient.java`
   - **New Method**: `raiseDispute(String merchantId, String accountNumber, String tranId, String disputeReason, String remarks)`
   - **Features**:
     - Validates authentication before making request
     - Accepts transaction ID (TranId) and dispute details
     - Includes error handling and logging
     - Returns dispute response as JSON string
     - Makes POST request to `/api/v1/CorporatePay/RaiseDispute`

### 4. **Updated Main Method**
   - **File**: `/src/main/java/com/example/FinacleAPIClient.java`
   - **Flow**:
     Step 1: Authenticate and get token
     Step 2: Retrieve corporate pay statement
     Step 3: Fetch transaction details (S544447560 from statement)
     Step 4: Raise dispute for that specific transaction with:
       - DisputeReason: "Unauthorized transaction"
       - Remarks: "Transaction not authorized by account holder"

### 5. **Updated Project Configuration**
   - **File**: `/pom.xml`
   - **Changes**:
     - Uses WireMock 3.3.1 library (already configured)
     - All necessary dependencies in place for HTTP calls and JSON processing

## API Request/Response Examples

### Request to Raise Dispute
```bash
POST /api/v1/CorporatePay/RaiseDispute HTTP/1.1
Authorization: Bearer {token}
Content-Type: application/json

{
  "MerchantId": "NCHL",
  "AccountNumber": "99000111165000011",
  "TranId": "S544447560",
  "DisputeReason": "Unauthorized transaction",
  "Remarks": "Transaction not authorized by account holder"
}
```

### Response
```json
{
  "Response": {
    "ResponseCode": "000",
    "ResponseMessage": "Dispute raised successfully"
  },
  "Result": {
    "DisputeId": "DSP20260507001",
    "TranId": "S544447560",
    "DisputeDate": "07/05/2026",
    "DisputeReason": "Unauthorized transaction",
    "Remarks": "Transaction not authorized by account holder",
    "Status": "INITIATED",
    "Amount": "100.00",
    "DisputeAmount": "100.00",
    "ReferenceNumber": "REF-DSP-20260507-001"
  }
}
```

## Data Flow

1. **Client Authentication**
   - POST to `/api/Auth` with credentials
   - Receive JWT token

2. **Fetch Statement**
   - POST to `/api/v1/CorporatePay/GetCorporatePayStatement` with token
   - Retrieve list of transactions with TranIds

3. **Raise Dispute**
   - Identify specific transaction (e.g., S544447560)
   - POST dispute to `/api/v1/CorporatePay/RaiseDispute` with:
     - TranId from the statement
     - DisputeReason (why the transaction is disputed)
     - Remarks (additional details about the dispute)
   - Receive DisputeId and confirmation

## Error Handling

The API client includes:
- Authentication validation before dispute submission
- HTTP status code checking (expects 200 OK)
- Exception handling with SLF4J logging
- Null checks and error messages

## Compilation & Build Status

✅ **Compilation**: SUCCESS
✅ **Build**: SUCCESS
- All 3 Java source files compile successfully
- Packaged as `wiremock-server.jar` (shaded JAR)
- Ready for deployment

## Testing

To test the implementation:

1. **Start WireMock Server**
   ```bash
   java -jar target/wiremock-server.jar
   ```

2. **Run Full Workflow (with client)**
   ```bash
   java -cp target/classes:target/dependency/* com.example.FinacleAPIClient
   ```

3. **Manual Testing with curl**
   ```bash
   # Get token
   curl -X POST http://localhost:8080/api/Auth \
     -H "Content-Type: application/json" \
     -d '{"UserName":"NCHL","Password":"TEST"}'

   # Raise dispute
   curl -X POST http://localhost:8080/api/v1/CorporatePay/RaiseDispute \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer {TOKEN}" \
     -d '{
       "MerchantId": "NCHL",
       "AccountNumber": "99000111165000011",
       "TranId": "S544447560",
       "DisputeReason": "Unauthorized transaction",
       "Remarks": "Transaction not authorized"
     }'
   ```

## Files Modified

1. ✅ `/mappings/07-raise-dispute.json` - **CREATED**
2. ✅ `/src/main/java/com/example/FinacleWireMockServer.java` - **MODIFIED**
3. ✅ `/src/main/java/com/example/FinacleAPIClient.java` - **MODIFIED**
4. ✅ `/pom.xml` - **CONFIGURED** (WireMock 3.3.1)

## Current Situation - No Errors

- ✅ All Java files compile successfully
- ✅ Project builds without errors
- ✅ All required dependencies are resolved
- ✅ WireMock server is properly configured
- ✅ API client has complete dispute workflow
- ✅ Dispute endpoint is fully mocked and functional

