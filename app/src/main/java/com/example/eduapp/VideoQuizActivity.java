package com.example.eduapp; // your app package name

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VideoQuizActivity extends AppCompatActivity {

    private WebView youtubeWebView;
    private LinearLayout quizContainer;
    private Button submitAnswerButton;
    private EditText answerInput;
    private TextView questionText;

    private Button chatWithAIButton;  // 👈 New button reference


    // A sample YouTube video ID (e.g., a short educational clip)
    private static final String YOUTUBE_VIDEO_ID = "jNQXAC9IVRw"; // Placeholder video ID

    // A sample question and answer that pops up
    private static final String QUIZ_QUESTION = "What is the key concept introduced in the first minute of the video?";
    private static final String CORRECT_ANSWER = "key concept"; // Simplified check for demonstration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_quiz);

        youtubeWebView = findViewById(R.id.youtubeWebView);
        quizContainer = findViewById(R.id.quizContainer);
        submitAnswerButton = findViewById(R.id.btnSubmitAnswer);
        answerInput = findViewById(R.id.answerInput);
        questionText = findViewById(R.id.questionText);
        chatWithAIButton = findViewById(R.id.btnChatWithAI);  // 👈 Connect button

        // 1. Setup the WebView for YouTube playback
        loadYoutubeVideo();

        // 2. Load the initial question
        questionText.setText(QUIZ_QUESTION);

        // 3. Handle submission logic
        submitAnswerButton.setOnClickListener(v -> {
            String submittedAnswer = answerInput.getText().toString().trim();
            if (submittedAnswer.isEmpty()) {
                Toast.makeText(this, "Please type your answer!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Simple validation logic (you'd use a real database check here)
            if (submittedAnswer.toLowerCase().contains("key concept")) {
                Toast.makeText(this, "Correct! Well done!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Review the video and try again.", Toast.LENGTH_LONG).show();
            }

            // Clear the input field for the next question (if any)
            answerInput.setText("");
        });

        // **TODO: Implement Timer Logic Here**
        // In a real app, you'd use a Handler/Runnable to hide/show the quizContainer
        // at specific video timestamps (e.g., after 30 seconds).

        chatWithAIButton.setOnClickListener(v -> {
            Intent intent = new Intent(VideoQuizActivity.this, ChatBotActivity.class);
            startActivity(intent);
        });
    }

    private void loadYoutubeVideo() {
        // Essential WebView settings for media playback
        youtubeWebView.getSettings().setJavaScriptEnabled(true);
        youtubeWebView.setWebChromeClient(new WebChromeClient());
        youtubeWebView.setWebViewClient(new WebViewClient());

        // The HTML structure needed to embed a full-screen YouTube player
        String frameVideo = "<html><body><iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/"
                + YOUTUBE_VIDEO_ID
                + "?autoplay=1&amp;modestbranding=1&amp;rel=0\" frameborder=\"0\" allowfullscreen></iframe></body></html>";

        youtubeWebView.loadData(frameVideo, "text/html", "utf-8");
    }
}