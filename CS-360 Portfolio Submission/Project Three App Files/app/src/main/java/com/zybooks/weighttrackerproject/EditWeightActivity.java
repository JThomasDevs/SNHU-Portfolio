package com.zybooks.weighttrackerproject;

import static java.util.Locale.US;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.sql.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditWeightActivity extends AppCompatActivity {

    private EditText editWeight;
    private EditText editMonth;
    private EditText editDay;
    private EditText editYear;
    private TextView errorText;
    private double weight;
    private String date;
    private int id;  // For DB queries
    private Spinner unitSpinner;
    private ExecutorService executor;
    private Handler handler;
    private WeightDatabase weightDb;
    private SharedPreferences sharedPrefs;
    private String WEIGHT_UNIT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_weight);

        sharedPrefs = getSharedPreferences("weight_prefs", MODE_PRIVATE);
        WEIGHT_UNIT = sharedPrefs.getString("unit", "lb");

        errorText = findViewById(R.id.error_save_weight_text);
        editWeight = findViewById(R.id.editWeight);
        editMonth = findViewById(R.id.editMonth);
        editDay = findViewById(R.id.editDay);
        editYear = findViewById(R.id.editYear);
        unitSpinner = findViewById(R.id.weightUnit);
        if ((WEIGHT_UNIT.equals("lb"))) {
            unitSpinner.setSelection(0);
        } else {
            unitSpinner.setSelection(1);
        }

        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        weightDb = new WeightDatabase(this);

        // Get the extras from the Intent
        Intent intent = getIntent();
        weight = intent.getDoubleExtra("weight", -1.0);
        date = intent.getStringExtra("date");
        id = intent.getIntExtra("id", -1);

        // Split the date String into day, month, and year
        String[] splitDate = date.split("-");
        String year = splitDate[0];
        String month = splitDate[1];
        String day = splitDate[2];

        // Set the EditTexts to the values from the Intent
        editWeight.setText(String.format(US, "%.1f", weight));
        editMonth.setText(month);
        editDay.setText(day);
        editYear.setText(year);
    }

    public void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }

    private String formatDate(String month, String day, String year) {
        return (year + "-" + month + "-" + day);
    }

    public void goBack(View view) {
        // Navigate back to the MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void saveWeight(View view) {
        // Gather data to build the Weight object and to check for data differences.
        // Check for empty weight String
        if (editWeight.getText().toString().isEmpty()) {
            errorText.setText(R.string.error_empty_weight_text);
            return;
        }
        double currentWeight = Double.parseDouble(editWeight.getText().toString());
        String month = editMonth.getText().toString();
        String day = editDay.getText().toString();
        String year = editYear.getText().toString();
        String currentDateString = formatDate(month, day, year);
        Date currentDate;
        try {
            currentDate = Date.valueOf(currentDateString);
        } catch (IllegalArgumentException e) {
            errorText.setText(R.string.error_date_text);
            return;
        }

        String unit = unitSpinner.getSelectedItem().toString();

        // Check for unit change
        if (!unit.equals(WEIGHT_UNIT)) {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString("unit", unit);
            editor.apply();
        }

        // Check for changes between the weight/date passed as extras and the values held in the EditText widgets when the Save button was pressed
        // If changes are detected, commit them to the DB and send user back to MainActivity screen
        if (currentWeight != weight || currentDate != Date.valueOf(date) || !unit.equals(WEIGHT_UNIT)) {
            executor.execute(() -> {
                Weight weightEntry = new Weight(id, currentWeight, currentDate, unit, getString(R.string.weight_button_text));
                int dbStatus = weightDb.editWeight(weightEntry);
                // dbStatus is the number of affected rows. > 0 means the weight was successfully updated
                if (dbStatus != 0) {
                    goBack(view);
                } else {
                    handler.post(() -> errorText.setText(R.string.error_save_weight_text));
                }
            });
        } else {
            // If NO changes are detected, nothing needs to be sent to the DB so we can safely navigate back to MainActivity screen
            goBack(view);
        }
    }

    public void deleteWeight(View view) {
        executor.execute(() -> {
            int dbStatus = weightDb.deleteWeight(id);
            if (dbStatus != 0) {
                goBack(view);
            } else {
                handler.post(() -> errorText.setText(R.string.error_delete_weight_text));
            }
        });
    }
}
