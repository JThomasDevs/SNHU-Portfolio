package com.zybooks.weighttrackerproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SetGoalActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private String WEIGHT_UNIT;
    private ExecutorService executor;
    private Handler handler;
    private WeightDatabase weightDb;
    private EditText editGoalWeight;
    private Spinner unitSpinner;
    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_goal);

        sharedPref = getSharedPreferences("weight_prefs", MODE_PRIVATE);
        WEIGHT_UNIT = sharedPref.getString("unit", "lb");

        editGoalWeight = findViewById(R.id.editGoalWeight);
        unitSpinner = findViewById(R.id.weightUnit);
        errorText = findViewById(R.id.error_goal_weight_text);

        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        weightDb = new WeightDatabase(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void setGoal(View view) {
        // Check for empty weight String
        if (editGoalWeight.getText().toString().isEmpty()) {
            errorText.setText(R.string.enter_goal_weight);
            return;
        }
        double weight = Double.parseDouble(editGoalWeight.getText().toString());
        String unit = unitSpinner.getSelectedItem().toString();

        // Check for unit changes
        if(!unit.equals(WEIGHT_UNIT)) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("unit", unit);
            editor.apply();
        }

        // Save the goal weight to the database
        executor.execute(() -> {
            long dbStatus = weightDb.updateGoalWeight(weight, unit);
            // If an error occurred with db.insert
            if (dbStatus == -1) {
                handler.post(() -> errorText.setText(R.string.error_saving_goal));
            }
            // If an error occurred with db.update
            else if (dbStatus == 0) {
                handler.post(() -> errorText.setText(R.string.error_saving_goal));
            }
            // Success
            else {
                goBack(view);
            }
        });
    }
}
