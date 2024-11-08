package com.example.goldencarrot.utils;

public interface FirestoreCallback<T> {
    void onSuccess(T result);
    void onFailure(Exception e);
}
