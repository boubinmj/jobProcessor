package com.example.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Smoke Tests for EventController
 * Uses pure Java HTTP client for integration testing without Spring Boot test dependencies
 */
public class EventControllerSmokeTestSimple {

    private static ConfigurableApplicationContext applicationContext;
    private static final String BASE_URL = "http://localhost:8080/api/events";
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    @BeforeAll
    public static void setUp() {
        // Start the Spring Boot application
        applicationContext = SpringApplication.run(com.example.MyApplication.class);
    }

    private HttpResponse<String> sendRequest(String method, String url, String body) throws Exception {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Content-Type", "application/json");

        if ("POST".equals(method)) {
            requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body != null ? body : ""));
        } else if ("GET".equals(method)) {
            requestBuilder.GET();
        }

        return httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
    }

    // ===================== POST /api/events Tests =====================

    @Test
    public void testSubmitEvent_Success() throws Exception {
        String payload = "{\"eventType\": \"RECEIVE_PAYMENT\", \"size\": 10}";
        HttpResponse<String> response = sendRequest("POST", BASE_URL, payload);
        
        assertEquals(201, response.statusCode(), "Expected status 201 (Created)");
        assertNotNull(response.body(), "Response body should not be null");
        assertTrue(response.body().contains("RECEIVE_PAYMENT"), "Response should contain event type");
    }

    @Test
    public void testSubmitEvent_InvalidEventType() throws Exception {
        String payload = "{\"eventType\": \"INVALID_TYPE\", \"size\": 5}";
        HttpResponse<String> response = sendRequest("POST", BASE_URL, payload);
        
        assertEquals(400, response.statusCode(), "Expected status 400 (Bad Request) for invalid event type");
    }

    @Test
    public void testSubmitEvent_MissingFields() throws Exception {
        String payload = "{\"eventType\": \"CREATE_ORDER\"}";
        HttpResponse<String> response = sendRequest("POST", BASE_URL, payload);
        
        assertEquals(500, response.statusCode(), "Expected status 500 (Internal Server Error) for missing fields");
    }

    // ===================== GET /api/events Tests =====================

    @Test
    public void testGetAllEvents_Success() throws Exception {
        HttpResponse<String> response = sendRequest("GET", BASE_URL, null);
        
        assertEquals(200, response.statusCode(), "Expected status 200 (OK)");
        assertNotNull(response.body(), "Response body should not be null");
        assertTrue(response.body().startsWith("["), "Response should be a JSON array");
    }

    @Test
    public void testGetEventsByStatus_Queued() throws Exception {
        HttpResponse<String> response = sendRequest("GET", BASE_URL + "?status=queued", null);
        
        assertEquals(200, response.statusCode(), "Expected status 200 (OK)");
        assertNotNull(response.body(), "Response body should not be null");
    }

    @Test
    public void testGetEventsByStatus_Running() throws Exception {
        HttpResponse<String> response = sendRequest("GET", BASE_URL + "?status=running", null);
        
        assertEquals(200, response.statusCode(), "Expected status 200 (OK)");
        assertNotNull(response.body(), "Response body should not be null");
    }

    @Test
    public void testGetEventsByStatus_Completed() throws Exception {
        HttpResponse<String> response = sendRequest("GET", BASE_URL + "?status=completed", null);
        
        assertEquals(200, response.statusCode(), "Expected status 200 (OK)");
        assertNotNull(response.body(), "Response body should not be null");
    }

    @Test
    public void testGetEventsByStatus_InvalidStatus() throws Exception {
        HttpResponse<String> response = sendRequest("GET", BASE_URL + "?status=invalid_status", null);
        
        assertEquals(400, response.statusCode(), "Expected status 400 (Bad Request) for invalid status");
    }

    @Test
    public void testGetEvent_NotFound() throws Exception {
        HttpResponse<String> response = sendRequest("GET", BASE_URL + "/invalid-id-12345", null);
        
        assertEquals(404, response.statusCode(), "Expected status 404 (Not Found)");
    }

    // ===================== GET /api/events/status/* Tests =====================

    @Test
    public void testGetQueuedEvents_Success() throws Exception {
        HttpResponse<String> response = sendRequest("GET", BASE_URL + "/status/queued", null);
        
        assertEquals(200, response.statusCode(), "Expected status 200 (OK)");
        assertNotNull(response.body(), "Response body should not be null");
    }

    @Test
    public void testGetRunningEvents_Success() throws Exception {
        HttpResponse<String> response = sendRequest("GET", BASE_URL + "/status/running", null);
        
        assertEquals(200, response.statusCode(), "Expected status 200 (OK)");
        assertNotNull(response.body(), "Response body should not be null");
    }

    @Test
    public void testGetCompletedEvents_Success() throws Exception {
        HttpResponse<String> response = sendRequest("GET", BASE_URL + "/status/completed", null);
        
        assertEquals(200, response.statusCode(), "Expected status 200 (OK)");
        assertNotNull(response.body(), "Response body should not be null");
    }

    // ===================== GET /api/events/metrics Tests =====================

    @Test
    public void testGetMetrics_Success() throws Exception {
        HttpResponse<String> response = sendRequest("GET", BASE_URL + "/metrics", null);
        
        assertEquals(200, response.statusCode(), "Expected status 200 (OK)");
        assertNotNull(response.body(), "Response body should not be null");
        assertTrue(response.body().startsWith("{"), "Response should be a JSON object");
    }
}
