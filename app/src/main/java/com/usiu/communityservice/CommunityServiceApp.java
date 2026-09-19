package com.usiu.communityservice;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class CommunityServiceApp extends Application {

    private static CommunityServiceApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Configure Firestore offline persistence for free-tier resilience
        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            db.setFirestoreSettings(settings);
        } catch (Exception ignored) {
            // In testing or mocked environments
        }
    }

    public static CommunityServiceApp getInstance() {
        return instance;
    }
}
