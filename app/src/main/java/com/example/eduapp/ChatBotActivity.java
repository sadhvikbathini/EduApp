//package com.example.eduapp;
//
//import androidx.appcompat.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.*;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import java.io.IOException;
//import okhttp3.*;
//
//public class ChatBotActivity extends AppCompatActivity {
//
//    private LinearLayout chatContainer;
//    private ScrollView scrollView;
//    private EditText userInput;
//    private Button sendBtn;
//
//    private static final String GEMINI_API_KEY = "AIzaSyAE8Jbd_n1UQ4LNrDIcx3-LeET6cINJ-lc";
//    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";
//
//    // Common Gemini models to try
//    private static final String[] MODELS_TO_TRY = {
//            "gemini-1.5-flash",
//            "gemini-2.5-flash",
//            "gemini-1.0-pro",
//            "gemini-2.0-pro",
//            "gemini-flash-latest",
//            "gemini-pro",
//            "gemini-2.5-pro",
//            "text-bison-001"  // Fallback to older PaLM model
//    };
//
//    private String currentWorkingModel = null;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chatbot);
//
//        chatContainer = findViewById(R.id.chatContainer);
//        scrollView = findViewById(R.id.scrollView);
//        userInput = findViewById(R.id.userInput);
//        sendBtn = findViewById(R.id.sendBtn);
//
//        // First, detect which models are available
//        addMessageToChat("Detecting available models...", false);
//        detectAvailableModels();
//
//        sendBtn.setOnClickListener(v -> {
//            String question = userInput.getText().toString().trim();
//            if (!question.isEmpty()) {
//                addMessageToChat(question, true);
//                userInput.setText("");
//                if (currentWorkingModel != null) {
//                    getGeminiResponse(question);
//                } else {
//                    addMessageToChat("No working model found. Please check your API key.", false);
//                }
//            }
//        });
//    }
//
//    private void detectAvailableModels() {
//        OkHttpClient client = new OkHttpClient();
//
//        // Test each model
//        for (String model : MODELS_TO_TRY) {
//            String testUrl = BASE_URL + model + "?key=" + GEMINI_API_KEY;
//
//            Request request = new Request.Builder()
//                    .url(testUrl)
//                    .get()
//                    .build();
//
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    runOnUiThread(() -> addMessageToChat("Failed to check model: " + model, false));
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    if (response.isSuccessful()) {
//                        runOnUiThread(() -> {
//                            if (currentWorkingModel == null) {
//                                currentWorkingModel = model;
//                                addMessageToChat("✓ Using model: " + model, false);
//                                addMessageToChat("Ready! Type your message below.", false);
//                            }
//                        });
//                    } else {
//                        runOnUiThread(() -> addMessageToChat("✗ Model not available: " + model, false));
//                    }
//                }
//            });
//        }
//    }
//
//    private void getGeminiResponse(String query) {
//        if (currentWorkingModel == null) {
//            addMessageToChat("No working model available. Please check your API key.", false);
//            return;
//        }
//
//        OkHttpClient client = new OkHttpClient();
//        String apiUrl = BASE_URL + currentWorkingModel + ":generateContent?key=" + GEMINI_API_KEY;
//
//        try {
//            JSONObject requestBody = new JSONObject();
//
//            if (currentWorkingModel.startsWith("text-bison")) {
//                // Use PaLM API format for text-bison
//                requestBody.put("prompt", new JSONObject().put("text", query));
//                requestBody.put("temperature", 0.7);
//                requestBody.put("maxOutputTokens", 512);
//            } else {
//                // Use Gemini API format
//                JSONObject textPart = new JSONObject();
//                textPart.put("text", query);
//
//                JSONObject contentItem = new JSONObject();
//                contentItem.put("parts", new JSONArray().put(textPart));
//
//                requestBody.put("contents", new JSONArray().put(contentItem));
//
//                JSONObject generationConfig = new JSONObject();
//                generationConfig.put("temperature", 0.7);
//                generationConfig.put("maxOutputTokens", 512);
//                requestBody.put("generationConfig", generationConfig);
//            }
//
//            RequestBody body = RequestBody.create(
//                    requestBody.toString(),
//                    MediaType.parse("application/json; charset=utf-8")
//            );
//
//            Request request = new Request.Builder()
//                    .url(apiUrl)
//                    .post(body)
//                    .addHeader("Content-Type", "application/json")
//                    .build();
//
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    runOnUiThread(() -> addMessageToChat("Network error: " + e.getMessage(), false));
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    String responseBody = response.body().string();
//                    runOnUiThread(() -> {
//                        if (response.isSuccessful()) {
//                            try {
//                                JSONObject json = new JSONObject(responseBody);
//                                String text;
//
//                                if (currentWorkingModel.startsWith("text-bison")) {
//                                    // Parse PaLM response
//                                    text = json.getJSONArray("candidates")
//                                            .getJSONObject(0)
//                                            .getString("output");
//                                } else {
//                                    // Parse Gemini response
//                                    text = json.getJSONArray("candidates")
//                                            .getJSONObject(0)
//                                            .getJSONObject("content")
//                                            .getJSONArray("parts")
//                                            .getJSONObject(0)
//                                            .getString("text");
//                                }
//
//                                addMessageToChat(text.trim(), false);
//                            } catch (Exception e) {
//                                addMessageToChat("Response parsing error: " + e.getMessage(), false);
//                            }
//                        } else {
//                            addMessageToChat("API Error: " + response.code() + ". Trying to find another model...", false);
//                            currentWorkingModel = null;
//                            detectAvailableModels();
//                        }
//                    });
//                }
//            });
//
//        } catch (Exception e) {
//            runOnUiThread(() -> addMessageToChat("Request error: " + e.getMessage(), false));
//        }
//    }
//
//    private void addMessageToChat(String message, boolean isUser) {
//        runOnUiThread(() -> {
//            TextView textView = new TextView(ChatBotActivity.this);
//            textView.setText(message);
//            textView.setTextSize(14f);
//            textView.setPadding(16, 12, 16, 12);
//
//            if (isUser) {
//                textView.setBackgroundColor(0xFFE3F2FD);
//            } else {
//                textView.setBackgroundColor(0xFFF5F5F5);
//            }
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//            );
//            params.setMargins(8, 8, 8, 8);
//            params.gravity = isUser ? Gravity.END : Gravity.START;
//            textView.setLayoutParams(params);
//
//            chatContainer.addView(textView);
//            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
//        });
//    }
//}


//package com.example.eduapp;
//
//import androidx.appcompat.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.*;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import java.io.IOException;
//import okhttp3.*;
//
//public class ChatBotActivity extends AppCompatActivity {
//
//    private LinearLayout chatContainer;
//    private ScrollView scrollView;
//    private EditText userInput;
//    private Button sendBtn;
//
//    private static final String GEMINI_API_KEY = "AIzaSyAE8Jbd_n1UQ4LNrDIcx3-LeET6cINJ-lc";
//    private String currentWorkingEndpoint = null;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chatbot);
//
//        chatContainer = findViewById(R.id.chatContainer);
//        scrollView = findViewById(R.id.scrollView);
//        userInput = findViewById(R.id.userInput);
//        sendBtn = findViewById(R.id.sendBtn);
//
//        addMessageToChat("Testing API access...", false);
//        testApiAccess();
//
//        sendBtn.setOnClickListener(v -> {
//            String question = userInput.getText().toString().trim();
//            if (!question.isEmpty()) {
//                addMessageToChat(question, true);
//                userInput.setText("");
//                if (currentWorkingEndpoint != null) {
//                    getAIResponse(question);
//                } else {
//                    addMessageToChat("No working API endpoint found.", false);
//                }
//            }
//        });
//    }
//
//    private void testApiAccess() {
//        // Test different API endpoints
//        String[] endpoints = {
//                // Gemini endpoints
//                "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent",
//                "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.0-pro:generateContent",
//                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent",
//                // PaLM endpoint (most likely to work with your key)
//                "https://generativelanguage.googleapis.com/v1beta/models/text-bison-001:generateText"
//        };
//
//        for (String endpoint : endpoints) {
//            testEndpoint(endpoint);
//        }
//    }
//
//    private void testEndpoint(String endpoint) {
//        OkHttpClient client = new OkHttpClient();
//        String testUrl = endpoint + "?key=" + GEMINI_API_KEY;
//
//        try {
//            JSONObject requestBody = new JSONObject();
//
//            if (endpoint.contains("text-bison")) {
//                // PaLM format
//                requestBody.put("prompt", new JSONObject().put("text", "Hello"));
//                requestBody.put("temperature", 0.1);
//                requestBody.put("maxOutputTokens", 10);
//            } else {
//                // Gemini format
//                JSONObject textPart = new JSONObject();
//                textPart.put("text", "Hello");
//
//                JSONObject contentItem = new JSONObject();
//                contentItem.put("parts", new JSONArray().put(textPart));
//
//                requestBody.put("contents", new JSONArray().put(contentItem));
//
//                JSONObject generationConfig = new JSONObject();
//                generationConfig.put("temperature", 0.1);
//                generationConfig.put("maxOutputTokens", 10);
//                requestBody.put("generationConfig", generationConfig);
//            }
//
//            RequestBody body = RequestBody.create(
//                    requestBody.toString(),
//                    MediaType.parse("application/json; charset=utf-8")
//            );
//
//            Request request = new Request.Builder()
//                    .url(testUrl)
//                    .post(body)
//                    .addHeader("Content-Type", "application/json")
//                    .build();
//
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    runOnUiThread(() -> {
//                        if (currentWorkingEndpoint == null) {
//                            addMessageToChat("✗ Failed: " + getEndpointName(endpoint), false);
//                        }
//                    });
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    String responseBody = response.body().string();
//                    runOnUiThread(() -> {
//                        if (response.isSuccessful()) {
//                            if (currentWorkingEndpoint == null) {
//                                currentWorkingEndpoint = endpoint;
//                                addMessageToChat("✓ Connected: " + getEndpointName(endpoint), false);
//                                addMessageToChat("Ready! Ask me anything.", false);
//                            }
//                        } else {
//                            if (currentWorkingEndpoint == null) {
//                                addMessageToChat("✗ Failed: " + getEndpointName(endpoint) + " (" + response.code() + ")", false);
//                            }
//                        }
//                    });
//                }
//            });
//
//        } catch (Exception e) {
//            runOnUiThread(() -> {
//                if (currentWorkingEndpoint == null) {
//                    addMessageToChat("✗ Error testing: " + getEndpointName(endpoint), false);
//                }
//            });
//        }
//    }
//
//    private String getEndpointName(String endpoint) {
//        if (endpoint.contains("gemini-pro")) return "Gemini Pro";
//        if (endpoint.contains("gemini-1.0-pro")) return "Gemini 1.0 Pro";
//        if (endpoint.contains("gemini-1.5-flash")) return "Gemini 1.5 Flash";
//        if (endpoint.contains("text-bison")) return "PaLM Text Bison";
//        return "Unknown";
//    }
//
//    private void getAIResponse(String query) {
//        OkHttpClient client = new OkHttpClient();
//        String apiUrl = currentWorkingEndpoint + "?key=" + GEMINI_API_KEY;
//
//        try {
//            JSONObject requestBody = new JSONObject();
//            boolean isPalm = currentWorkingEndpoint.contains("text-bison");
//
//            if (isPalm) {
//                // PaLM API format
//                requestBody.put("prompt", new JSONObject().put("text", query));
//                requestBody.put("temperature", 0.7);
//                requestBody.put("maxOutputTokens", 512);
//                requestBody.put("topK", 40);
//                requestBody.put("topP", 0.95);
//            } else {
//                // Gemini API format
//                JSONObject textPart = new JSONObject();
//                textPart.put("text", query);
//
//                JSONObject contentItem = new JSONObject();
//                contentItem.put("parts", new JSONArray().put(textPart));
//
//                requestBody.put("contents", new JSONArray().put(contentItem));
//
//                JSONObject generationConfig = new JSONObject();
//                generationConfig.put("temperature", 0.7);
//                generationConfig.put("maxOutputTokens", 512);
//                generationConfig.put("topK", 40);
//                generationConfig.put("topP", 0.95);
//                requestBody.put("generationConfig", generationConfig);
//            }
//
//            RequestBody body = RequestBody.create(
//                    requestBody.toString(),
//                    MediaType.parse("application/json; charset=utf-8")
//            );
//
//            Request request = new Request.Builder()
//                    .url(apiUrl)
//                    .post(body)
//                    .addHeader("Content-Type", "application/json")
//                    .build();
//
//            addMessageToChat("Thinking...", false);
//
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    runOnUiThread(() -> {
//                        removeThinkingMessage();
//                        addMessageToChat("Network error: " + e.getMessage(), false);
//                    });
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    String responseBody = response.body().string();
//                    runOnUiThread(() -> {
//                        removeThinkingMessage();
//
//                        if (response.isSuccessful()) {
//                            try {
//                                String responseText = parseAIResponse(responseBody, currentWorkingEndpoint.contains("text-bison"));
//                                if (responseText != null) {
//                                    addMessageToChat(responseText, false);
//                                } else {
//                                    addMessageToChat("Failed to parse response. Raw: " + responseBody, false);
//                                }
//                            } catch (Exception e) {
//                                addMessageToChat("Error: " + e.getMessage(), false);
//                            }
//                        } else {
//                            addMessageToChat("API Error " + response.code() + ". Raw: " + responseBody, false);
//                            // Reset and retest
//                            currentWorkingEndpoint = null;
//                            addMessageToChat("Re-testing API endpoints...", false);
//                            testApiAccess();
//                        }
//                    });
//                }
//            });
//
//        } catch (Exception e) {
//            runOnUiThread(() -> {
//                removeThinkingMessage();
//                addMessageToChat("Request error: " + e.getMessage(), false);
//            });
//        }
//    }
//
//    private String parseAIResponse(String responseBody, boolean isPalm) {
//        try {
//            JSONObject json = new JSONObject(responseBody);
//
//            if (isPalm) {
//                // PaLM response format
//                if (json.has("candidates")) {
//                    JSONArray candidates = json.getJSONArray("candidates");
//                    if (candidates.length() > 0) {
//                        JSONObject candidate = candidates.getJSONObject(0);
//                        if (candidate.has("output")) {
//                            return candidate.getString("output").trim();
//                        }
//                    }
//                }
//                // Alternative PaLM format
//                if (json.has("predictions")) {
//                    JSONArray predictions = json.getJSONArray("predictions");
//                    if (predictions.length() > 0) {
//                        return predictions.getJSONObject(0).getString("content").trim();
//                    }
//                }
//            } else {
//                // Gemini response format - try multiple possible structures
//
//                // Standard Gemini format
//                if (json.has("candidates")) {
//                    JSONArray candidates = json.getJSONArray("candidates");
//                    if (candidates.length() > 0) {
//                        JSONObject candidate = candidates.getJSONObject(0);
//
//                        // Try content->parts->text
//                        if (candidate.has("content")) {
//                            JSONObject content = candidate.getJSONObject("content");
//                            if (content.has("parts")) {
//                                JSONArray parts = content.getJSONArray("parts");
//                                if (parts.length() > 0 && parts.getJSONObject(0).has("text")) {
//                                    return parts.getJSONObject(0).getString("text").trim();
//                                }
//                            }
//                        }
//
//                        // Try direct text
//                        if (candidate.has("text")) {
//                            return candidate.getString("text").trim();
//                        }
//                    }
//                }
//
//                // Alternative Gemini format
//                if (json.has("text")) {
//                    return json.getString("text").trim();
//                }
//            }
//
//            // If no standard format found, try to extract any text content
//            return extractAnyText(json);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private String extractAnyText(JSONObject json) {
//        try {
//            // Convert entire JSON to string and look for text patterns
//            String jsonString = json.toString();
//
//            // Simple heuristic: look for the first substantial text content
//            if (jsonString.contains("\"text\"")) {
//                int textStart = jsonString.indexOf("\"text\"") + 7;
//                int textEnd = jsonString.indexOf("\"", textStart);
//                if (textEnd > textStart) {
//                    String text = jsonString.substring(textStart, textEnd);
//                    if (text.length() > 5) { // Minimum reasonable length
//                        return text.trim();
//                    }
//                }
//            }
//
//            return "I received your message but had trouble parsing the response.";
//
//        } catch (Exception e) {
//            return "Response received but format unexpected.";
//        }
//    }
//
//    private void removeThinkingMessage() {
//        if (chatContainer.getChildCount() > 0) {
//            View lastChild = chatContainer.getChildAt(chatContainer.getChildCount() - 1);
//            if (lastChild instanceof TextView) {
//                TextView lastText = (TextView) lastChild;
//                if ("Thinking...".equals(lastText.getText().toString())) {
//                    chatContainer.removeView(lastChild);
//                }
//            }
//        }
//    }
//
//    private void addMessageToChat(String message, boolean isUser) {
//        runOnUiThread(() -> {
//            TextView textView = new TextView(ChatBotActivity.this);
//            textView.setText(message);
//            textView.setTextSize(16f);
//            textView.setPadding(32, 16, 32, 16);
//
//            if (isUser) {
//                textView.setBackgroundColor(0xFFE3F2FD);
//                textView.setGravity(Gravity.END);
//            } else {
//                textView.setBackgroundColor(0xFFF5F5F5);
//                textView.setGravity(Gravity.START);
//            }
//
//            // Create rounded corners
//            textView.setBackgroundResource(isUser ? R.drawable.user_bubble : R.drawable.bot_bubble);
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//            );
//            params.setMargins(16, 8, 16, 8);
//            params.gravity = isUser ? Gravity.END : Gravity.START;
//            textView.setLayoutParams(params);
//
//            chatContainer.addView(textView);
//            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
//        });
//    }
//}



package com.example.eduapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.*;

public class ChatBotActivity extends AppCompatActivity {

    private LinearLayout chatContainer;
    private ScrollView scrollView;
    private EditText userInput;
    private Button sendBtn;

    private static final String GEMINI_API_KEY = "AIzaSyAE8Jbd_n1UQ4LNrDIcx3-LeET6cINJ-lc";
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + GEMINI_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        chatContainer = findViewById(R.id.chatContainer);
        scrollView = findViewById(R.id.scrollView);
        userInput = findViewById(R.id.userInput);
        sendBtn = findViewById(R.id.sendBtn);

        addMessageToChat("Gemini 2.5 Flash Chatbot Ready!", false);
        addMessageToChat("Ask me anything...", false);

        sendBtn.setOnClickListener(v -> {
            String question = userInput.getText().toString().trim();
            if (!question.isEmpty()) {
                addMessageToChat(question, true);
                userInput.setText("");
                getGeminiResponse(question);
            }
        });
    }

    private void getGeminiResponse(String query) {
        OkHttpClient client = new OkHttpClient();

        try {
            // Simplified request - use the exact format from your working curl
            JSONObject textPart = new JSONObject();
            textPart.put("text", query);

            JSONObject contentItem = new JSONObject();
            contentItem.put("parts", new JSONArray().put(textPart));

            JSONObject requestBody = new JSONObject();
            requestBody.put("contents", new JSONArray().put(contentItem));

            // Use minimal generation config
            JSONObject generationConfig = new JSONObject();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("maxOutputTokens", 100); // Reduced from 512

            requestBody.put("generationConfig", generationConfig);

            RequestBody body = RequestBody.create(
                    requestBody.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(GEMINI_URL)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            addMessageToChat("Thinking...", false);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        removeThinkingMessage();
                        addMessageToChat("Network error: " + e.getMessage(), false);
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    runOnUiThread(() -> {
                        removeThinkingMessage();

                        if (response.isSuccessful()) {
                            try {
                                JSONObject json = new JSONObject(responseBody);
                                String responseText = parseGeminiResponse(json);

                                if (responseText != null) {
                                    addMessageToChat(responseText, false);
                                } else {
                                    // Check for finishReason errors
                                    String errorMessage = checkForErrors(json);
                                    if (errorMessage != null) {
                                        addMessageToChat(errorMessage, false);
                                    } else {
                                        addMessageToChat("No response generated. Try asking again.", false);
                                    }
                                }

                            } catch (Exception e) {
                                addMessageToChat("Error: " + e.getMessage(), false);
                            }
                        } else {
                            addMessageToChat("API Error " + response.code() + ": " + responseBody, false);
                        }
                    });
                }
            });

        } catch (Exception e) {
            runOnUiThread(() -> {
                removeThinkingMessage();
                addMessageToChat("Request error: " + e.getMessage(), false);
            });
        }
    }

    private String parseGeminiResponse(JSONObject json) {
        try {
            if (json.has("candidates")) {
                JSONArray candidates = json.getJSONArray("candidates");
                if (candidates.length() > 0) {
                    JSONObject firstCandidate = candidates.getJSONObject(0);

                    // Check if content exists and has parts with text
                    if (firstCandidate.has("content")) {
                        JSONObject content = firstCandidate.getJSONObject("content");

                        if (content.has("parts")) {
                            JSONArray parts = content.getJSONArray("parts");
                            if (parts.length() > 0) {
                                JSONObject firstPart = parts.getJSONObject(0);
                                if (firstPart.has("text")) {
                                    String text = firstPart.getString("text");
                                    if (!text.isEmpty()) {
                                        return text.trim();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String checkForErrors(JSONObject json) {
        try {
            if (json.has("candidates")) {
                JSONArray candidates = json.getJSONArray("candidates");
                if (candidates.length() > 0) {
                    JSONObject firstCandidate = candidates.getJSONObject(0);

                    // Check finish reason
                    if (firstCandidate.has("finishReason")) {
                        String finishReason = firstCandidate.getString("finishReason");
                        switch (finishReason) {
                            case "MAX_TOKENS":
                                return "Response too long. Try a shorter question.";
                            case "SAFETY":
                                return "Response blocked for safety reasons.";
                            case "RECITATION":
                                return "Content matched blocked material.";
                            case "OTHER":
                                return "The model stopped for unknown reasons.";
                        }
                    }

                    // Check if content is empty
                    if (firstCandidate.has("content")) {
                        JSONObject content = firstCandidate.getJSONObject("content");
                        if (!content.has("parts") || content.getJSONArray("parts").length() == 0) {
                            return "No response generated. The model returned empty content.";
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private void removeThinkingMessage() {
        if (chatContainer.getChildCount() > 0) {
            View lastChild = chatContainer.getChildAt(chatContainer.getChildCount() - 1);
            if (lastChild instanceof TextView) {
                TextView lastText = (TextView) lastChild;
                if ("Thinking...".equals(lastText.getText().toString())) {
                    chatContainer.removeView(lastChild);
                }
            }
        }
    }

    private void addMessageToChat(String message, boolean isUser) {
        runOnUiThread(() -> {
            TextView textView = new TextView(ChatBotActivity.this);
            textView.setText(message);
            textView.setTextSize(16f);
            textView.setPadding(32, 16, 32, 16);

            if (isUser) {
                textView.setBackgroundColor(0xFFE3F2FD); // Light blue
                textView.setGravity(Gravity.END);
            } else {
                textView.setBackgroundColor(0xFFF5F5F5); // Light gray
                textView.setGravity(Gravity.START);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(16, 8, 16, 8);
            params.gravity = isUser ? Gravity.END : Gravity.START;
            textView.setLayoutParams(params);

            chatContainer.addView(textView);
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
        });
    }
}