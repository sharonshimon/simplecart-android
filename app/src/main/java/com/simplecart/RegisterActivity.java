package com.simplecart;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

public class RegisterActivity extends Activity {
    EditText etUsername, etPassword, etPhone;
    Button btnRegister;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            String phone = etPhone.getText().toString();
            if (username.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (db.isUsernameTaken(username)) {
                Toast.makeText(this, "Username taken", Toast.LENGTH_SHORT).show();
                return;
            }
            if (db.addUser(username, password, phone)) {
                Toast.makeText(this, "Registered! Please login.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
