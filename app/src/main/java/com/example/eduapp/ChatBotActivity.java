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

    private static final String GEMINI_API_KEY = "";
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

    // Common Gemini models to try
    private static final String[] MODELS_TO_TRY = {
            "gemini-1.5-flash",
            "gemini-2.5-flash",
            "gemini-1.0-pro",
            "gemini-2.0-pro",
            "gemini-flash-latest",
            "gemini-pro",
            "gemini-2.5-pro",
            "text-bison-001"  // Fallback to older PaLM model
    };

    private String currentWorkingModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        chatContainer = findViewById(R.id.chatContainer);
        scrollView = findViewById(R.id.scrollView);
        userInput = findViewById(R.id.userInput);
        sendBtn = findViewById(R.id.sendBtn);

        // First, detect which models are available
        addMessageToChat("Detecting available models...", false);
        detectAvailableModels();

        sendBtn.setOnClickListener(v -> {
            String question = userInput.getText().toString().trim();
            if (!question.isEmpty()) {
                addMessageToChat(question, true);
                userInput.setText("");
                if (currentWorkingModel != null) {
                    getGeminiResponse(question);
                } else {
                    addMessageToChat("No working model found. Please check your API key.", false);
                }
            }
        });
    }

    private void detectAvailableModels() {
        OkHttpClient client = new OkHttpClient();

        // Test each model
        for (String model : MODELS_TO_TRY) {
            String testUrl = BASE_URL + model + "?key=" + GEMINI_API_KEY;

            Request request = new Request.Builder()
                    .url(testUrl)
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> addMessageToChat("Failed to check model: " + model, false));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            if (currentWorkingModel == null) {
                                currentWorkingModel = model;
                                addMessageToChat("✓ Using model: " + model, false);
                                addMessageToChat("Ready! Type your message below.", false);
                            }
                        });
                    } else {
                        runOnUiThread(() -> addMessageToChat("✗ Model not available: " + model, false));
                    }
                }
            });
        }
    }

    private void getGeminiResponse(String query) {
        if (currentWorkingModel == null) {
            addMessageToChat("No working model available. Please check your API key.", false);
            return;
        }

        OkHttpClient client = new OkHttpClient();
        String apiUrl = BASE_URL + currentWorkingModel + ":generateContent?key=" + GEMINI_API_KEY;

        try {
            JSONObject requestBody = new JSONObject();

            if (currentWorkingModel.startsWith("text-bison")) {
                // Use PaLM API format for text-bison
                requestBody.put("prompt", new JSONObject().put("text", query));
                requestBody.put("temperature", 0.7);
                requestBody.put("maxOutputTokens", 512);
            } else {
                // Use Gemini API format
                JSONObject textPart = new JSONObject();
                textPart.put("text", query);

                JSONObject contentItem = new JSONObject();
                contentItem.put("parts", new JSONArray().put(textPart));

                requestBody.put("contents", new JSONArray().put(contentItem));

                JSONObject generationConfig = new JSONObject();
                generationConfig.put("temperature", 0.7);
                generationConfig.put("maxOutputTokens", 512);
                requestBody.put("generationConfig", generationConfig);
            }

            RequestBody body = RequestBody.create(
                    requestBody.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> addMessageToChat("Network error: " + e.getMessage(), false));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject json = new JSONObject(responseBody);
                                String text;

                                if (currentWorkingModel.startsWith("text-bison")) {
                                    // Parse PaLM response
                                    text = json.getJSONArray("candidates")
                                            .getJSONObject(0)
                                            .getString("output");
                                } else {
                                    // Parse Gemini response
                                    text = json.getJSONArray("candidates")
                                            .getJSONObject(0)
                                            .getJSONObject("content")
                                            .getJSONArray("parts")
                                            .getJSONObject(0)
                                            .getString("text");
                                }

                                addMessageToChat(text.trim(), false);
                            } catch (Exception e) {
                                addMessageToChat("Response parsing error: " + e.getMessage(), false);
                            }
                        } else {
                            addMessageToChat("API Error: " + response.code() + ". Trying to find another model...", false);
                            currentWorkingModel = null;
                            detectAvailableModels();
                        }
                    });
                }
            });

        } catch (Exception e) {
            runOnUiThread(() -> addMessageToChat("Request error: " + e.getMessage(), false));
        }
    }

    private void addMessageToChat(String message, boolean isUser) {
        runOnUiThread(() -> {
            TextView textView = new TextView(ChatBotActivity.this);
            textView.setText(message);
            textView.setTextSize(14f);
            textView.setPadding(16, 12, 16, 12);

            if (isUser) {
                textView.setBackgroundColor(0xFFE3F2FD);
            } else {
                textView.setBackgroundColor(0xFFF5F5F5);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 8, 8, 8);
            params.gravity = isUser ? Gravity.END : Gravity.START;
            textView.setLayoutParams(params);

            chatContainer.addView(textView);
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
        });
    }
}