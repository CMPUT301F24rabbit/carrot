package com.example.goldencarrot.authentication;

import com.google.firebase.auth.FirebaseUser;

public interface AccountService {
    void signUp(String email, String password, AuthCallback callback);

    void signIn(String email, String password, AuthCallback callback);

    interface AuthCallback {
        void onAuthSuccess(FirebaseUser user);
        void onAuthFailure();
    }
}
