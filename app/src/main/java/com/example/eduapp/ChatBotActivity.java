//package com.example.eduapp;
//
//import androidx.appcompat.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.*;
//import org.json.JSONObject;
//import java.io.IOException;
//import okhttp3.*;
//
//public class ChatBotActivity extends AppCompatActivity {
//
//    EditText userInput;
//    TextView chatResponse;
//    Button sendBtn;
//
//    private static final String GEMINI_API_KEY = "AIzaSyAE8Jbd_n1UQ4LNrDIcx3-LeET6cINJ-lc"; // replace
//    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + GEMINI_API_KEY;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chatbot);
//
//        userInput = findViewById(R.id.userInput);
//        chatResponse = findViewById(R.id.chatResponse);
//        sendBtn = findViewById(R.id.sendBtn);
//
//        sendBtn.setOnClickListener(v -> {
//            String input = userInput.getText().toString();
//            if (!input.isEmpty()) {
//                getGeminiResponse(input);
//            }
//        });
//    }
//
//    private void getGeminiResponse(String query) {
//        OkHttpClient client = new OkHttpClient();
//
//        JSONObject content = new JSONObject();
//        try {
//            JSONObject part = new JSONObject();
//            part.put("text", query);
//
//            JSONObject contentObj = new JSONObject();
//            contentObj.put("parts", new org.json.JSONArray().put(part));
//
//            content.put("contents", new org.json.JSONArray().put(contentObj));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        RequestBody body = RequestBody.create(
//                content.toString(),
//                MediaType.parse("application/json")
//        );
//
//        Request request = new Request.Builder()
//                .url(GEMINI_URL)
//                .post(body)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                runOnUiThread(() -> chatResponse.setText("Error: " + e.getMessage()));
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    try {
//                        String res = response.body().string();
//                        JSONObject json = new JSONObject(res);
//                        String text = json
//                                .getJSONArray("candidates")
//                                .getJSONObject(0)
//                                .getJSONObject("content")
//                                .getJSONArray("parts")
//                                .getJSONObject(0)
//                                .getString("text");
//
//                        runOnUiThread(() -> chatResponse.setText(text.trim()));
//                    } catch (Exception e) {
//                        runOnUiThread(() -> chatResponse.setText("Parsing error: " + e.getMessage()));
//                    }
//                } else {
//                    runOnUiThread(() -> chatResponse.setText("Error: " + response.message()));
//                }
//            }
//        });
//    }
//}
package com.example.eduapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.*;

public class ChatBotActivity extends AppCompatActivity {

    private LinearLayout chatContainer;
    private ScrollView scrollView;
    private EditText userInput;
    private Button sendBtn;

    private static final String GEMINI_API_KEY = "AIzaSyAE8Jbd_n1UQ4LNrDIcx3-LeET6cINJ-lc"; // Replace with your key
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/text-bison-001:generateMessage?key=" + GEMINI_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        chatContainer = findViewById(R.id.chatContainer);
        scrollView = findViewById(R.id.scrollView);
        userInput = findViewById(R.id.userInput);
        sendBtn = findViewById(R.id.sendBtn);

        sendBtn.setOnClickListener(v -> {
            String question = userInput.getText().toString().trim();
            if (!question.isEmpty()) {
                addMessageToChat(question, true); // User message
                userInput.setText("");
                getGeminiResponse(question);
            }
        });
    }

    private void getGeminiResponse(String query) {
        OkHttpClient client = new OkHttpClient();

        try {
            JSONObject message = new JSONObject();
            message.put("author", "user");

            JSONObject textPart = new JSONObject();
            textPart.put("type", "text");
            textPart.put("text", query);

            message.put("content", new org.json.JSONArray().put(textPart));

            JSONObject content = new JSONObject();
            content.put("messages", new org.json.JSONArray().put(message));
            content.put("temperature", 0.7);

            RequestBody body = RequestBody.create(
                    content.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(GEMINI_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> addMessageToChat("Network error: " + e.getMessage(), false));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String res = response.body().string();
                            JSONObject json = new JSONObject(res);
                            // Parse response text
                            String text = json.getJSONArray("candidates")
                                    .getJSONObject(0)
                                    .getJSONObject("content")
                                    .getJSONArray("text")
                                    .getString(0);

                            runOnUiThread(() -> addMessageToChat(text.trim(), false)); // Bot message
                        } catch (Exception e) {
                            runOnUiThread(() -> addMessageToChat("Parsing error: " + e.getMessage(), false));
                        }
                    } else {
                        runOnUiThread(() -> addMessageToChat("API Error: " + response.code() + " " + response.message(), false));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            addMessageToChat("Request error: " + e.getMessage(), false);
        }
    }

    private void addMessageToChat(String message, boolean isUser) {
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setTextSize(16f);
        textView.setPadding(16, 12, 16, 12);
        textView.setBackgroundResource(isUser ? R.drawable.user_bubble : R.drawable.bot_bubble);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 8, 8, 8);
        params.gravity = isUser ? Gravity.END : Gravity.START;
        textView.setLayoutParams(params);

        chatContainer.addView(textView);

        // Scroll to bottom
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
}
