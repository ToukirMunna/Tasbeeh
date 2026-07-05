# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Essential attributes for Gson and Runtime checks
-keepattributes SourceFile,LineNumberTable,Signature,InnerClasses,EnclosingMethod,*Annotation*

# Gson specific classes - Keep them intact
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.** { *; }

# Prevent ProGuard from stripping interface information from TypeAdapterFactory, etc.
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Explicitly keep any class that extends TypeToken (Crucial for the crash you are seeing)
-keep class * extends com.google.gson.reflect.TypeToken

# ----------------------------------------------------------------------------
# LEAST AGGRESSIVE STRATEGY:
# Keep ALL of your own code. We only want to shrink the libraries (Compose, AndroidX).
# This prevents the minifier from breaking your logic or data classes.
# ----------------------------------------------------------------------------
-keep class com.toukir.tasbeeh.** { *; }

# If you use other libraries that need keeping, add them here.
# But usually, just keeping your own package is enough to prevent logical breaks
# while still removing megabytes of unused Compose/AndroidX code.
