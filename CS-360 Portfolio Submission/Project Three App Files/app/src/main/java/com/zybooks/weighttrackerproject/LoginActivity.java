package com.zybooks.weighttrackerproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private WeightDatabase weightDb;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView errorTextView;
    private ExecutorService executor;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set the default weight unit to lb
        SharedPreferences sharedPrefs = getSharedPreferences("weight_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("unit", "lb");
        editor.apply();

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        errorTextView = findViewById(R.id.error_login_text);
        loginButton = findViewById(R.id.login_button);
        weightDb = new WeightDatabase(this);

        // Create ExecutorService and Handler to perform background tasks
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        // Use a background thread to check if the login table is empty and adjust the login button text accordingly
        executor.execute(() -> {
            final boolean isLoginTableEmpty = weightDb.isLoginTableEmpty();

            handler.post(() -> {
                if (isLoginTableEmpty) {
                    loginButton.setText(R.string.register);
                } else {
                    loginButton.setText(R.string.login);
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }

    public void login(View view) {
        String username = usernameEditText.getText().toString().trim();
        String password = hashPassword(passwordEditText.getText().toString().trim());
        int passwordLength = passwordEditText.getText().toString().trim().length();
        if (username.length() < 1) {
            errorTextView.setText(R.string.username_required);
        } else if (passwordLength < 8) {
            errorTextView.setText(R.string.password_length);
        } else {
            executor.execute(() -> {
                final boolean isLoginTableEmpty = weightDb.isLoginTableEmpty();

                if (isLoginTableEmpty) {
                    // If no users are registered, register the user
                    long userId = weightDb.registerUser(username, password);
                    if (userId == -1) {
                        // If this is -1, an error occurred while writing the information to the database
                        handler.post(() -> errorTextView.setText(R.string.register_error));
                    } else {
                        // Otherwise, the user was successfully registered and should be navigated to the main screen
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    }
                } else {
                    // Otherwise, validate the user's credentials
                    final boolean isValidLogin = weightDb.validateLogin(username, password);

                    handler.post(() -> {
                        if (isValidLogin) {
                            // If the credentials are valid, navigate to the main screen
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // Otherwise, display an error message
                            errorTextView.setText(R.string.invalid_login);
                        }
                    });
                }
            });
        }
    }

    private String hashPassword(String password) {
        // Hash the password using SHA-256
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hashPassword = new StringBuilder();
        for (byte b : hash) {
            hashPassword.append(String.format("%02x", b));
        }
        return hashPassword.toString();
    }
}
