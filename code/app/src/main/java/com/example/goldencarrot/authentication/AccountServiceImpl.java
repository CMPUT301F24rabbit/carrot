package com.example.goldencarrot.authentication;

import android.content.Context;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountServiceImpl implements AccountService {
    private FirebaseAuth mAuth;
    private Context context;

    // Constructor to initialize FirebaseAuth and Context
    public AccountServiceImpl(Context context) {
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void signUp(final String email, final String password, final AccountService.AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign up success, pass the signed-in user to the callback
                        FirebaseUser user = mAuth.getCurrentUser();
                        callback.onAuthSuccess(user);
                    } else {
                        // If sign up fails, pass null to the callback
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(context, "Authentication failed: " + errorMessage, Toast.LENGTH_LONG).show();
                        callback.onAuthFailure();
                    }
                });
    }

    @Override
    public void signIn(String email, String password, AccountService.AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, pass the signed-in user to the callback
                        FirebaseUser user = mAuth.getCurrentUser();
                        callback.onAuthSuccess(user);
                    } else {
                        // If sign in fails, pass null to the callback
                        Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        callback.onAuthFailure();
                    }
                });
    }
}