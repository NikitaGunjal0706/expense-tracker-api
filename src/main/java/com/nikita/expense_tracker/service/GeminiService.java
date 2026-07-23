package com.nikita.expense_tracker.service;

import com.nikita.expense_tracker.model.Expense;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String askAboutExpenses(String question, List<Expense> expenses) {
        // Build a plain-text summary of expenses to feed as context
        StringBuilder expenseSummary = new StringBuilder();
        for (Expense e : expenses) {
            expenseSummary.append("- ")
                    .append(e.getCategory())
                    .append(": ")
                    .append(e.getAmount())
                    .append(" on ")
                    .append(e.getDate())
                    .append("\n");
        }

        String prompt = "You are an assistant analyzing a user's personal expenses. "
                + "Here is the expense data:\n" + expenseSummary
                + "\nBased only on this data, answer the following question:\n" + question;

        // Build Gemini request body
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        String urlWithKey = apiUrl + "?key=" + apiKey;

        try {
            Map<String, Object> response = restTemplate.postForObject(urlWithKey, requestEntity, Map.class);
            return extractText(response);
        } catch (Exception e) {
            return "Error contacting AI service: " + e.getMessage();
        }
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> response) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            return "Could not parse AI response.";
        }
    }
}