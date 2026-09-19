# Proguard rules for CommunityServiceApp
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
    @com.google.firebase.firestore.PropertyName <methods>;
}
-keep class com.usiu.communityservice.data.models.** { *; }
