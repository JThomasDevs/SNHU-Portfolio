package com.zybooks.weighttrackerproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.Manifest;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPrefs;
    private SharedPreferences notificationPrefs;
    private String WEIGHT_UNIT;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private WeightDatabase weightDb;
    private List<Weight> weightEntries;
    private ExecutorService executor;
    private Handler handler;
    private TextView goalWeightDisplay;
    private ImageButton notificationBell;
    private boolean enableNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        goalWeightDisplay = findViewById(R.id.goalWeightDisplay);
        notificationBell = findViewById(R.id.notificationButton);

        // Get the weight unit from SharedPreferences
        sharedPrefs = getSharedPreferences("weight_prefs", MODE_PRIVATE);
        WEIGHT_UNIT = sharedPrefs.getString("unit", "lb");

        // Read notification preferences and set the notification bell icon accordingly
        notificationPrefs = getSharedPreferences("notification_prefs", MODE_PRIVATE);
        enableNotifications = notificationPrefs.getBoolean("notification", false);
        if (enableNotifications) {
            notificationBell.setImageResource(R.drawable.notification_bell);
            notificationBell.setTag("enabled");
        } else {
            notificationBell.setImageResource(R.drawable.notification_bell_disabled);
            notificationBell.setTag("disabled");
        }

        weightDb = new WeightDatabase(this);
        weightEntries = weightDb.readAllWeights(getString(R.string.weight_button_text), WEIGHT_UNIT);

        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        // Set the RecyclerView to display the weight entries in a grid
        recyclerView = findViewById(R.id.data_grid);
        gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        // Use the DataGridAdapter to display the weight entries in the RecyclerView
        // This will create a grid of buttons, each displaying a weight entry for each weight gathered from the DB
        DataGridAdapter dataGridAdapter = new DataGridAdapter(this, weightEntries);
        recyclerView.setAdapter(dataGridAdapter);

        setGoalWeightDisplay();

        // Check if the user has hit their goal weight
        if (weightEntries.size() > 0) {
            double goalWeight = weightDb.readGoalWeight(WEIGHT_UNIT);
            // If there is a goal weight set
            if (goalWeight != -1) {
                // Read the most recent weight entry by Date
                double lastWeight = weightEntries.get(0).getWeight();
                // If the most recent weight entry is less than or equal to the goal weight, send an SMS alert
                if (lastWeight <= goalWeight) {
                    sendSMSAlert();
                }
            }
        }
    }

    public void signOut(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void addWeight(View view) {
        Intent intent = new Intent(this, AddWeightActivity.class);
        startActivity(intent);
    }

    public void setGoal(View view) {
        Intent intent = new Intent(this, SetGoalActivity.class);
        startActivity(intent);
    }

    private void setGoalWeightDisplay() {
        executor.execute(() -> {
            double goalWeight = weightDb.readGoalWeight(WEIGHT_UNIT);
            if (goalWeight != -1) {
                handler.post(() -> goalWeightDisplay.setText(getString(R.string.goal_weight, goalWeight, WEIGHT_UNIT)));
            } else {
                handler.post(() -> goalWeightDisplay.setText(R.string.not_set));
            }
        });
    }

    // There is no way to RELIABLY gather the device's phone number without manually requesting it from the user. Because this app is a school assignment and will likely only be tested on emulated devices which have no phone number, using READ_PHONE_NUMBERS is the best we can do.
    // However, when tested on my development machine, using the Pixel3a API 34 emulator, the alert was successfully sent to the phone number associated with the emulator.
    private void sendSMSAlert() {
        if (!enableNotifications) {
            return;
        }
        // If permission has been granted to send SMS and to read phone state (to get phone number)
        if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            // Get the device's phone number
            String phoneNumber = telephonyManager.getLine1Number();

            // If the phone number is not null, request permission to send notifications
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, "You have hit your goal weight. Congratulations!", null, null);
            } else {
                // If the phone number is null, notify the user of success with a Toast message
                Toast.makeText(this, "You have hit your goal weight. Congratulations!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Enable and disable the sending of SMS notifications
    public void toggleNotifications(View view) {
        // If permission has not been granted to send SMS and to read phone state (to get phone number)
        if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
            // This is needed to avoid issues caused if one permission is granted but the other is not at first
            List<String> permissionsToRequest = new ArrayList<>();
            if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.SEND_SMS);
            }
            if (checkSelfPermission(Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_PHONE_NUMBERS);
            }
            requestPermissions(permissionsToRequest.toArray(new String[0]), 0);
        } else {
            // If notifications are enabled, disable them
            if (notificationBell.getTag() == "enabled") {
                // If the notification bell icon is enabled, disable it
                notificationBell.setImageResource(R.drawable.notification_bell_disabled);
                notificationBell.setTag("disabled");
                // Save the notification preference to SharedPreferences
                SharedPreferences.Editor editor = notificationPrefs.edit();
                editor.putBoolean("notification", false).apply();
                Toast.makeText(this, "Notifications disabled", Toast.LENGTH_SHORT).show();
            } else {
                // If notifications are disabled, enable them
                notificationBell.setImageResource(R.drawable.notification_bell);
                notificationBell.setTag("enabled");
                // Save the notification preference to SharedPreferences
                SharedPreferences.Editor editor = notificationPrefs.edit();
                editor.putBoolean("notification", true).apply();
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If permission has been granted, change the notification bell icon to indicate that notifications can be sent
                notificationBell.setImageResource(R.drawable.notification_bell);
                notificationBell.setTag("enabled");
                // Save the notification preference to SharedPreferences
                SharedPreferences.Editor editor = notificationPrefs.edit();
                editor.putBoolean("notification", true).apply();
            } else {
                // If permission has not been granted, change the notification bell icon to indicate that notifications cannot be sent
                notificationBell.setImageResource(R.drawable.notification_bell_disabled);
                notificationBell.setTag("disabled");
                // Save the notification preference to SharedPreferences
                SharedPreferences.Editor editor = notificationPrefs.edit();
                editor.putBoolean("notification", false).apply();
            }
        }
    }
}