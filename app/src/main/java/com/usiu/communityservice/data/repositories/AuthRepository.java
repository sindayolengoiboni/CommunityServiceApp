package com.usiu.communityservice.data.repositories;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.util.Constants;

public class AuthRepository {
    private static AuthRepository instance;
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public interface UserCallback {
        void onSuccess(User user);
        void onError(Exception e);
    }

    public interface ActionCallback {
        void onSuccess();
        void onError(Exception e);
    }

    private AuthRepository() {
        this.auth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    public static synchronized AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

    public FirebaseUser getCurrentFirebaseUser() {
        return auth.getCurrentUser();
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public void signOut() {
        auth.signOut();
    }

    public Task<AuthResult> login(String email, String password) {
        return auth.signInWithEmailAndPassword(email.trim(), password);
    }

    public void registerStudent(String email, String password, String fullName, String studentId,
                                String phone, String studentStatus, String serviceOption,
                                ActionCallback callback) {
        auth.createUserWithEmailAndPassword(email.trim(), password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser fUser = authResult.getUser();
                    if (fUser == null) {
                        callback.onError(new Exception("Registration failed: User is null"));
                        return;
                    }

                    // Strict security rule: Public registration ONLY allows STUDENT role
                    User user = new User(fUser.getUid(), fullName, email.trim(), Constants.ROLE_STUDENT);
                    user.setStudentId(studentId.trim());
                    user.setPhone(phone.trim());
                    user.setStudentStatus(studentStatus);
                    user.setServiceOption(serviceOption);

                    // Hands-on is eligible by default; Project requires employment verification
                    if (Constants.OPTION_PROJECT.equals(serviceOption)) {
                        user.setEligibilityStatus(Constants.ELIGIBILITY_PENDING);
                    } else {
                        user.setEligibilityStatus(Constants.ELIGIBILITY_APPROVED);
                    }

                    db.collection(Constants.COLL_USERS)
                            .document(fUser.getUid())
                            .set(user)
                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                            .addOnFailureListener(callback::onError);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getCurrentUserProfile(UserCallback callback) {
        FirebaseUser fUser = auth.getCurrentUser();
        if (fUser == null) {
            callback.onError(new Exception("No user is currently signed in."));
            return;
        }

        db.collection(Constants.COLL_USERS)
                .document(fUser.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        User user = doc.toObject(User.class);
                        if (user != null) {
                            if (!user.isActive()) {
                                auth.signOut();
                                callback.onError(new Exception("This account has been disabled by an administrator."));
                                return;
                            }
                            callback.onSuccess(user);
                        } else {
                            callback.onError(new Exception("Failed to parse user profile."));
                        }
                    } else {
                        callback.onError(new Exception("User profile not found in database."));
                    }
                })
                .addOnFailureListener(callback::onError);
    }

    public Task<Void> sendPasswordReset(String email) {
        return auth.sendPasswordResetEmail(email.trim());
    }

    public void updateUserProfile(User user, ActionCallback callback) {
        if (user == null || user.getUid() == null) {
            callback.onError(new Exception("Invalid user."));
            return;
        }
        db.collection(Constants.COLL_USERS)
                .document(user.getUid())
                .set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }
}
