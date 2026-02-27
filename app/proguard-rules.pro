# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep data classes used with Gson
-keep class com.mobilegiants.megila.data.** { *; }

# Media3 / ExoPlayer
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Lottie
-dontwarn com.airbnb.lottie.**

# Pushwoosh
-keep class com.pushwoosh.** { *; }
-dontwarn com.pushwoosh.**

# Keep custom views referenced in XML layouts
-keep class com.mobilegiants.megila.custom_views.** { *; }
