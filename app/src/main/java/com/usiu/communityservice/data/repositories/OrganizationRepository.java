package com.usiu.communityservice.data.repositories;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.usiu.communityservice.data.models.Opportunity;
import com.usiu.communityservice.data.models.Organization;
import com.usiu.communityservice.util.Constants;

import java.util.List;

public class OrganizationRepository {
    private static OrganizationRepository instance;
    private final FirebaseFirestore db;

    public interface ListCallback<T> {
        void onSuccess(List<T> items);
        void onError(Exception e);
    }

    public interface ItemCallback<T> {
        void onSuccess(T item);
        void onError(Exception e);
    }

    public interface ActionCallback {
        void onSuccess();
        void onError(Exception e);
    }

    private OrganizationRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public static synchronized OrganizationRepository getInstance() {
        if (instance == null) {
            instance = new OrganizationRepository();
        }
        return instance;
    }

    public void getApprovedOrganizations(ListCallback<Organization> callback) {
        db.collection(Constants.COLL_ORGANIZATIONS)
                .whereEqualTo("approved", true)
                .whereEqualTo("active", true)
                .get()
                .addOnSuccessListener(snap -> callback.onSuccess(snap.toObjects(Organization.class)))
                .addOnFailureListener(callback::onError);
    }

    public void getAllOrganizations(ListCallback<Organization> callback) {
        db.collection(Constants.COLL_ORGANIZATIONS)
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snap -> callback.onSuccess(snap.toObjects(Organization.class)))
                .addOnFailureListener(callback::onError);
    }

    public void getOrganizationById(String orgId, ItemCallback<Organization> callback) {
        db.collection(Constants.COLL_ORGANIZATIONS)
                .document(orgId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        callback.onSuccess(doc.toObject(Organization.class));
                    } else {
                        callback.onError(new Exception("Organization not found"));
                    }
                })
                .addOnFailureListener(callback::onError);
    }

    public void saveOrganization(Organization org, ActionCallback callback) {
        DocumentReference ref;
        if (org.getId() == null || org.getId().isEmpty()) {
            ref = db.collection(Constants.COLL_ORGANIZATIONS).document();
            org.setId(ref.getId());
        } else {
            ref = db.collection(Constants.COLL_ORGANIZATIONS).document(org.getId());
        }

        ref.set(org)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }

    public void getApprovedOpportunities(ListCallback<Opportunity> callback) {
        db.collection(Constants.COLL_OPPORTUNITIES)
                .whereEqualTo("active", true)
                .get()
                .addOnSuccessListener(snap -> callback.onSuccess(snap.toObjects(Opportunity.class)))
                .addOnFailureListener(callback::onError);
    }

    public void saveOpportunity(Opportunity opp, ActionCallback callback) {
        DocumentReference ref;
        if (opp.getId() == null || opp.getId().isEmpty()) {
            ref = db.collection(Constants.COLL_OPPORTUNITIES).document();
            opp.setId(ref.getId());
        } else {
            ref = db.collection(Constants.COLL_OPPORTUNITIES).document(opp.getId());
        }

        ref.set(opp)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }
}
