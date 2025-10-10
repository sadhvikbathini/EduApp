package com.example.eduapp; // your app package name


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
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

            if (selectedId == -1) {
                Toast.makeText(this, "Please select a goal", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selected = findViewById(selectedId);
            String goal = selected.getText().toString();

            Toast.makeText(this, "Goal selected: " + goal, Toast.LENGTH_SHORT).show();

            // Move to VideoQuizActivity
            Intent intent = new Intent(GoalSelectionActivity.this, VideoQuizActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("email", email);
            intent.putExtra("goal", goal);
            startActivity(intent);
            finish();
        });
    }
}

