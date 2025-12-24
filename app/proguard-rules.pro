# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep line number information for debugging stack traces
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to hide the original source file name
-renamesourcefileattribute SourceFile

#-------------------------------------------
# TensorFlow Lite
#-------------------------------------------
-keep class org.tensorflow.** { *; }
-keep interface org.tensorflow.** { *; }
-dontwarn org.tensorflow.**

#-------------------------------------------
# ML Kit Face Detection
#-------------------------------------------
-keep class com.google.mlkit.** { *; }
-keep interface com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

#-------------------------------------------
# Room Database
#-------------------------------------------
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class *
-keepclassmembers @androidx.room.Entity class * {
    <fields>;
}
-keep class * extends androidx.room.RoomDatabase$Callback { *; }

#-------------------------------------------
# Gson
#-------------------------------------------
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep Gson serialized/deserialized classes
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

#-------------------------------------------
# Kotlin Coroutines
#-------------------------------------------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

#-------------------------------------------
# Jetpack Compose
#-------------------------------------------
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
-keep class kotlin.Metadata { *; }

# Keep Composable functions
-keepclasseswithmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

#-------------------------------------------
# AndroidX / Lifecycle
#-------------------------------------------
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

#-------------------------------------------
# WorkManager
#-------------------------------------------
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context,androidx.work.WorkerParameters);
}

#-------------------------------------------
# Keep application classes
#-------------------------------------------
-keep class com.example.facialrecognition.** { *; }

#-------------------------------------------
# General Android rules
#-------------------------------------------
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep enum values
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
