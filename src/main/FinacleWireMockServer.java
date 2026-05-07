package com.example;

import com.google.gson.JsonObject;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


public class FinacleWireMockServer {
    private static final Logger logger = LoggerFactory.getLogger(FinacleWireMockServer.class);

    public static void main(String[] args) throws InterruptedException {
        // Create WireMock server on port 8080
        WireMockServer wireMockServer = new WireMockServer(8080);
        wireMockServer.start();

        logger.info("WireMock Server started on port 8080");

        // Configure mock endpoints
        configureMocks(wireMockServer);

        logger.info("Mock APIs configured successfully");
        logger.info("Available endpoints:");
        logger.info("  POST http://localhost:8080/api/Auth - Get authentication token");
        logger.info("  POST http://localhost:8080/api/v1/CorporatePay/GetCorporatePayStatement - Get statement");
        logger.info("  POST http://localhost:8080/api/v1/CorporatePay/RaiseDispute - Raise dispute for transaction");

        // Keep the server running
        Thread.currentThread().join();
    }

    private static void configureMocks(WireMockServer wireMockServer) {
        // Mock 1: Authentication Token Endpoint
        wireMockServer.stubFor(
            post(urlEqualTo("/api/Auth"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(generateTokenResponse()))
        );

        // Mock 2: Corporate Pay Statement Endpoint (with valid token)
        wireMockServer.stubFor(
            post(urlEqualTo("/api/v1/CorporatePay/GetCorporatePayStatement"))
                .withHeader("Authorization", matching("Bearer .*"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(generateStatementResponse()))
        );

        // Mock 4a: Raise Dispute with valid TranId S544447560
        wireMockServer.stubFor(
            post(urlEqualTo("/api/v1/CorporatePay/RaiseDispute"))
                .withHeader("Authorization", matching("Bearer .*"))
                .withRequestBody(matchingJsonPath("$.TranId", equalTo("S544447560")))
                .atPriority(1)
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(generateDisputeResponse("S544447560", "100.00", "DSP20260507001")))
        );

        // Mock 4b: Raise Dispute with valid TranId S544447561
        wireMockServer.stubFor(
            post(urlEqualTo("/api/v1/CorporatePay/RaiseDispute"))
                .withHeader("Authorization", matching("Bearer .*"))
                .withRequestBody(matchingJsonPath("$.TranId", equalTo("S544447561")))
                .atPriority(1)
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(generateDisputeResponse("S544447561", "5000.00", "DSP20260507002")))
        );

        // Mock 4c: Raise Dispute with valid TranId S544447562
        wireMockServer.stubFor(
            post(urlEqualTo("/api/v1/CorporatePay/RaiseDispute"))
                .withHeader("Authorization", matching("Bearer .*"))
                .withRequestBody(matchingJsonPath("$.TranId", equalTo("S544447562")))
                .atPriority(1)
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(generateDisputeResponse("S544447562", "2000.00", "DSP20260507003")))
        );

        // Mock 4d: Raise Dispute with INVALID TranId (not in statement)
        wireMockServer.stubFor(
            post(urlEqualTo("/api/v1/CorporatePay/RaiseDispute"))
                .withHeader("Authorization", matching("Bearer .*"))
                .atPriority(100)
                .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", "application/json")
                    .withBody(generateDisputeErrorResponse("Invalid TranId", "The specified TranId does not exist in the statement")))
        );

        // Configure advanced scenarios
        AdvancedWireMockConfig.configureAdvancedScenarios(wireMockServer);
    }

    private static String generateTokenResponse() {
        JsonObject response = new JsonObject();
        response.addProperty("token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1lIjoiQWRtaW4iLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3JvbGUiOiJBZG1pbiIsImp0aSI6IjljN2UzNjQzLTY2ZjctNDk0NS1iYzRiLWJlYzc4Yzc1Y2E2NCIsImV4cCI6MTc2OTY4MzIyMSwiaXNzIjoiaHR0cHM6Ly93d3cubm1iYmFuay5jb20ubnAifQ.BAJh1I3zeODheYAu7ZXPTVc65ZBrwhrKfMdntyLMPo0");
        response.addProperty("token_type", "Bearer");
        response.addProperty("Status", true);
        response.addProperty("message", "Success");
        return response.toString();
    }

    private static String generateStatementResponse() {
        JsonObject response = new JsonObject();

        // Response metadata
        JsonObject responseObj = new JsonObject();
        responseObj.addProperty("ResponseCode", "000");
        responseObj.addProperty("ResponseMessage", "Success");
        response.add("Response", responseObj);

        // Sample transactions matching 02-statement-with-token.json
        com.google.gson.JsonArray transactions = new com.google.gson.JsonArray();

        // Transaction 1
        JsonObject transaction1 = new JsonObject();
        transaction1.addProperty("SN", 1);
        transaction1.addProperty("TranId", "S544447560");
        transaction1.addProperty("TranSrlNo", "1");
        transaction1.addProperty("TranDate", "10/04/2024");
        transaction1.addProperty("TranAmount", "100.00");
        transaction1.addProperty("TranType", "D");
        transaction1.add("ChequeNumber", null);
        transaction1.addProperty("TranParticular", "48313213123/NTC PREPAID/984000000");
        transaction1.addProperty("TranRemarks", "98416XXX1630//190031");
        transaction1.addProperty("Balance", "1,036,206,629.39");
        transactions.add(transaction1);

        // Transaction 2
        JsonObject transaction2 = new JsonObject();
        transaction2.addProperty("SN", 2);
        transaction2.addProperty("TranId", "S544447561");
        transaction2.addProperty("TranSrlNo", "2");
        transaction2.addProperty("TranDate", "09/04/2024");
        transaction2.addProperty("TranAmount", "5000.00");
        transaction2.addProperty("TranType", "C");
        transaction2.add("ChequeNumber", null);
        transaction2.addProperty("TranParticular", "PAYMENT RECEIVED");
        transaction2.addProperty("TranRemarks", "Online Transfer");
        transaction2.addProperty("Balance", "1,036,206,529.39");
        transactions.add(transaction2);

        // Transaction 3
        JsonObject transaction3 = new JsonObject();
        transaction3.addProperty("SN", 3);
        transaction3.addProperty("TranId", "S544447562");
        transaction3.addProperty("TranSrlNo", "3");
        transaction3.addProperty("TranDate", "08/04/2024");
        transaction3.addProperty("TranAmount", "2000.00");
        transaction3.addProperty("TranType", "D");
        transaction3.add("ChequeNumber", null);
        transaction3.addProperty("TranParticular", "SALARY DISBURSEMENT");
        transaction3.addProperty("TranRemarks", "Monthly Salary");
        transaction3.addProperty("Balance", "1,036,201,529.39");
        transactions.add(transaction3);

        response.add("Result", transactions);
        return response.toString();
    }

    private static String generateDisputeResponse(String tranId, String amount, String disputeId) {
        JsonObject response = new JsonObject();
        JsonObject responseObj = new JsonObject();
        responseObj.addProperty("ResponseCode", "000");
        responseObj.addProperty("ResponseMessage", "Dispute raised successfully");
        response.add("Response", responseObj);

        JsonObject result = new JsonObject();
        result.addProperty("DisputeId", disputeId);
        result.addProperty("TranId", tranId);
        result.addProperty("DisputeDate", "07/05/2026");
        result.addProperty("DisputeReason", "Unauthorized transaction");
        result.addProperty("Remarks", "Transaction not authorized by account holder");
        result.addProperty("Status", "INITIATED");
        result.addProperty("Amount", amount);
        result.addProperty("DisputeAmount", amount);
        result.addProperty("ReferenceNumber", "REF-DSP-" + disputeId);

        response.add("Result", result);
        return response.toString();
    }

    private static String generateDisputeErrorResponse(String errorCode, String errorMessage) {
        JsonObject response = new JsonObject();
        JsonObject responseObj = new JsonObject();
        responseObj.addProperty("ResponseCode", "400");
        responseObj.addProperty("ResponseMessage", errorMessage);
        response.add("Response", responseObj);

        JsonObject result = new JsonObject();
        result.addProperty("ErrorCode", errorCode);
        result.addProperty("ErrorMessage", errorMessage);
        result.addProperty("ErrorDetails", "Please provide a valid TranId that exists in the statement");

        response.add("Result", result);
        return response.toString();
    }
}
