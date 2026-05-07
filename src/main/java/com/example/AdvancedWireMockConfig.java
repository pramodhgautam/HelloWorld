package com.example;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


/**
 * Advanced WireMock Configuration with complex scenarios
 * This class provides additional mock endpoints and scenarios
 */
public class AdvancedWireMockConfig {
    private static final Logger logger = LoggerFactory.getLogger(AdvancedWireMockConfig.class);

    /**
     * Configure advanced mock scenarios
     */
    public static void configureAdvancedScenarios(WireMockServer wireMockServer) {
        logger.info("Configuring advanced WireMock scenarios...");
        // Advanced scenarios disabled for standalone version
    }

    /**
     * Mock invalid credentials scenario
     */
    // Disabled for wiremock-standalone
    /*
    private static void configureInvalidCredentials(WireMockServer wireMockServer) {
        // ...existing code...
    }
    */

    /**
     * Mock statement not found scenario
     */
    // Disabled for wiremock-standalone

    /**
     * Mock server error scenario
     */
    // Disabled for wiremock-standalone

    /**
     * Mock dynamic statement based on account number
     * This generates different transaction counts based on the account number
     */
    // Disabled for wiremock-standalone

    /**
     * Generate dynamic transaction data
     */
    public static JsonObject generateDynamicStatement(String merchantId, String accountNumber,
                                                      String startDate, String endDate) {
        JsonObject response = new JsonObject();
        response.addProperty("Status", true);
        response.addProperty("message", "Success");
        response.addProperty("MerchantId", merchantId);
        response.addProperty("AccountNumber", accountNumber);
        response.addProperty("StartDate", startDate);
        response.addProperty("EndDate", endDate);

        // Generate transactions between startDate and endDate
        JsonArray transactions = new JsonArray();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        Random random = new Random();
        int transactionCount = 5 + random.nextInt(10); // 5-14 transactions

        LocalDate currentDate = start;
        int txnCounter = 1;

        while (!currentDate.isAfter(end) && txnCounter <= transactionCount) {
            double amount = 100 + random.nextDouble() * 9900;
            String type = random.nextBoolean() ? "Credit" : "Debit";

            JsonObject transaction = new JsonObject();
            transaction.addProperty("TransactionId", "TXN" + String.format("%06d", txnCounter));
            transaction.addProperty("Date", currentDate.toString());
            transaction.addProperty("Description",
                type.equals("Credit") ? "Payment received" : "Transfer out");
            transaction.addProperty("Amount", String.format("%.2f", amount));
            transaction.addProperty("Type", type);

            transactions.add(transaction);
            currentDate = currentDate.plusDays(random.nextInt(5) + 1);
            txnCounter++;
        }

        response.add("Transactions", transactions);
        return response;
    }

    /**
     * Generate sample data for testing
     */
    public static JsonObject generateSampleTransactions() {
        JsonObject response = new JsonObject();
        JsonArray transactions = new JsonArray();

        String[] descriptions = {
            "Payment received from customer",
            "Transfer out to supplier",
            "Salary disbursement",
            "Invoice payment",
            "Refund issued",
            "Interest credit"
        };

        for (int i = 1; i <= 10; i++) {
            JsonObject transaction = new JsonObject();
            transaction.addProperty("TransactionId", "TXN" + String.format("%06d", i));
            transaction.addProperty("Date", String.format("2024-01-%02d", (i % 28) + 1));
            transaction.addProperty("Description", descriptions[i % descriptions.length]);
            transaction.addProperty("Amount", 1000 + (i * 500));
            transaction.addProperty("Type", i % 2 == 0 ? "Credit" : "Debit");

            transactions.add(transaction);
        }

        response.add("Transactions", transactions);
        return response;
    }
}

