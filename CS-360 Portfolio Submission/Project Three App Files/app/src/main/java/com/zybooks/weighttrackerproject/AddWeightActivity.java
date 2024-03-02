package com.zybooks.weighttrackerproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import java.sql.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddWeightActivity extends AppCompatActivity {

    private EditText editWeight;
    private EditText editMonth;
    private EditText editDay;
    private EditText editYear;
    private Spinner unitSpinner;
    private TextView errorText;
    private SharedPreferences sharedPrefs;
    private String WEIGHT_UNIT;
    private ExecutorService executor;
    private Handler handler;
    private WeightDatabase weightDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weight);

        sharedPrefs = getSharedPreferences("weight_prefs", MODE_PRIVATE);
        WEIGHT_UNIT = sharedPrefs.getString("unit", "lb");

        errorText = findViewById(R.id.error_save_weight_text);
        editWeight = findViewById(R.id.editWeight);
        editMonth = findViewById(R.id.editMonth);
        editDay = findViewById(R.id.editDay);
        editYear = findViewById(R.id.editYear);
        unitSpinner = findViewById(R.id.weightUnit);
        // Assign the Spinner's default selection based on the shared preferences
        if ((WEIGHT_UNIT.equals("lb"))) {
            unitSpinner.setSelection(0);
        } else {
            unitSpinner.setSelection(1);
        }

        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(getMainLooper());
        weightDb = new WeightDatabase(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }

    private String formatDate(String month, String day, String year) {
        // Format the date as "YYYY-MM-DD" for the DB
        return (year + "-" + month + "-" + day);
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void saveWeight(View view) {
        // Get the weight and date from the EditTexts
        // Check for empty weight string
        if (editWeight.getText().toString().isEmpty()) {
            errorText.setText(R.string.error_empty_weight_text);
            return;
        }
        double weight = Double.parseDouble(editWeight.getText().toString());
        String month = editMonth.getText().toString();
        String day = editDay.getText().toString();
        String year = editYear.getText().toString();
        String dateString = formatDate(month, day, year);
        Date date;
        try {
            date = Date.valueOf(dateString);
        } catch (IllegalArgumentException e) {
            errorText.setText(R.string.error_date_text);
            return;
        }

        // Get the weight unit from the Spinner
        String unit = unitSpinner.getSelectedItem().toString();
        // Update the shared preferences if the unit has changed
        if (!unit.equals(WEIGHT_UNIT)) {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString("unit", unit);
            editor.apply();
        }

        executor.execute(() -> {
            Weight weightEntry = new Weight(weight, date, unit, getString(R.string.weight_button_text));
            long dbStatus = weightDb.addWeight(weightEntry);
            if (dbStatus != -1) {
                goBack(view);
            } else {
                handler.post(() -> errorText.setText(R.string.error_save_weight_text));
            }
        });


    }
}
