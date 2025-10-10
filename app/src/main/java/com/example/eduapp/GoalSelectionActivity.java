package com.example.eduapp; // your app package name

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class GoalSelectionActivity extends AppCompatActivity {
    RadioGroup goalGroup;
    Button btnSubmitGoal;
    String name, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_selection);

        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        goalGroup = findViewById(R.id.goalGroup);
        btnSubmitGoal = findViewById(R.id.btnSubmitGoal);

        btnSubmitGoal.setOnClickListener(v -> {
            int selectedId = goalGroup.getCheckedRadioButtonId();
            if(selectedId == -1){
                Toast.makeText(this, "Select a goal", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selected = findViewById(selectedId);
            String goal = selected.getText().toString();

            User user = new User(name, email, password, goal);

            ApiClient.getAuthAPI().signup(user).enqueue(new Callback<String>() {

                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        // Success: API returned 2xx code
                        Toast.makeText(GoalSelectionActivity.this, "Signup success! Navigating.", Toast.LENGTH_SHORT).show();

                        // 🟢 NAVIGATION NOW RUNS HERE, ONLY ON SUCCESS
                        Intent intent = new Intent(GoalSelectionActivity.this, VideoQuizActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Failure: API returned non-2xx code (e.g., 400 Bad Request)
                        String error = "Signup Failed: " + response.code() + " - " + response.message();
                        Toast.makeText(GoalSelectionActivity.this, error, Toast.LENGTH_LONG).show();
                        // You can log the error body here for debugging: Log.e("API_CALL", response.errorBody().string());
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    // Failure: No connection or network error
                    Toast.makeText(GoalSelectionActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}

