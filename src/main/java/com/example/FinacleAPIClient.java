package com.example;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.http.HttpRequest.BodyPublishers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FinacleAPIClient {
    private static final Logger logger = LoggerFactory.getLogger(FinacleAPIClient.class);
    private static final String BASE_URL = "http://localhost:8080";
    private final HttpClient httpClient;
    private String authToken;

    public FinacleAPIClient() {
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Authenticate and get token
     */
    public boolean authenticate(String username, String password) {
        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("UserName", username);
            requestBody.addProperty("Password", password);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/Auth"))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(requestBody.toString()))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject responseBody = JsonParser.parseString(response.body()).getAsJsonObject();
                this.authToken = responseBody.get("token").getAsString();
                logger.info("Authentication successful");
                logger.info("Token: {}", authToken);
                return true;
            } else {
                logger.error("Authentication failed with status: {}", response.statusCode());
                return false;
            }
        } catch (Exception e) {
            logger.error("Error during authentication", e);
            return false;
        }
    }

    /**
     * Get corporate pay statement
     */
    public String getCorporatePayStatement(String merchantId, String accountNumber,
                                          String startDate, String endDate) {
        if (authToken == null) {
            logger.error("Not authenticated. Please authenticate first.");
            return null;
        }

        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("MerchantId", merchantId);
            requestBody.addProperty("AccountNumber", accountNumber);
            requestBody.addProperty("StartDate", startDate);
            requestBody.addProperty("EndDate", endDate);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/v1/CorporatePay/GetCorporatePayStatement"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .POST(BodyPublishers.ofString(requestBody.toString()))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                logger.info("Statement retrieved successfully");
                return response.body();
            } else {
                logger.error("Failed to retrieve statement: {}", response.statusCode());
                return null;
            }
        } catch (Exception e) {
            logger.error("Error retrieving statement", e);
            return null;
        }
    }

    /**
     * Raise a dispute for a transaction
     * @param merchantId Merchant ID
     * @param accountNumber Account Number
     * @param tranId Transaction ID to dispute
     * @param disputeReason Reason for the dispute
     * @param remarks Additional remarks about the dispute
     * @return Dispute response as JSON string
     */
    public String raiseDispute(String merchantId, String accountNumber, String tranId,
                              String disputeReason, String remarks) {
        if (authToken == null) {
            logger.error("Not authenticated. Please authenticate first.");
            return null;
        }

        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("MerchantId", merchantId);
            requestBody.addProperty("AccountNumber", accountNumber);
            requestBody.addProperty("TranId", tranId);
            requestBody.addProperty("DisputeReason", disputeReason);
            requestBody.addProperty("Remarks", remarks);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/v1/CorporatePay/RaiseDispute"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .POST(BodyPublishers.ofString(requestBody.toString()))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                logger.info("Dispute raised successfully for TranId: {}", tranId);
                return response.body();
            } else {
                logger.error("Failed to raise dispute: {}", response.statusCode());
                return null;
            }
        } catch (Exception e) {
            logger.error("Error raising dispute", e);
            return null;
        }
    }

    public static void main(String[] args) {
        logger.info("Starting Finacle API Client");

        FinacleAPIClient client = new FinacleAPIClient();

        // Step 1: Authenticate
        logger.info("Step 1: Authenticating...");
        boolean authenticated = client.authenticate("NCHL", "TEST");

        if (authenticated) {
            // Step 2: Get statement
            logger.info("Step 2: Retrieving corporate pay statement...");
            String statement = client.getCorporatePayStatement(
                "NCHL",
                "99000111165000011",
                "2024-01-01",
                "2025-01-04"
            );

            if (statement != null) {
                logger.info("Statement Response:");
                logger.info(statement);

                // Step 3: Raise dispute for a transaction
                logger.info("Step 3: Raising dispute for transaction...");
                String disputeResponse = client.raiseDispute(
                    "NCHL",
                    "99000111165000011",
                    "S544447560",  // TranId from the statement
                    "Unauthorized transaction",
                    "Transaction not authorized by account holder"
                );

                if (disputeResponse != null) {
                    logger.info("Dispute Response:");
                    logger.info(disputeResponse);
                }
            }
        } else {
            logger.error("Failed to authenticate");
        }
    }
}

