# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# ---------------------------------------------------------------------------
# General: keep line numbers, annotations, generics + inner/enclosing info so
# crash stack traces stay readable and reflection-based libraries work.
# ---------------------------------------------------------------------------
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature,InnerClasses,EnclosingMethod
-keepattributes *Annotation*,Exceptions
-keep class kotlin.Metadata { *; }

# ---------------------------------------------------------------------------
# Gson (reflection-based serialization)
#
# FavoriteAppsService stores the favorite-apps list as JSON in SharedPreferences
# (key "favorite_apps_list") and AuthService stores UserModel as JSON (key
# "user_data"). Gson maps JSON keys directly to Kotlin field names, so R8 MUST
# NOT rename or strip these fields — otherwise data written by one build can't
# be read back by another (favorites disappear, user data resets).
# ---------------------------------------------------------------------------
-keep class com.rofiq.launcherly.features.favorite_apps.model.** { *; }
-keep class com.rofiq.launcherly.features.auth.model.** { *; }

# Keep any field explicitly annotated with @SerializedName.
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Preserve generic type signatures so TypeToken<List<FavoriteAppModel>> still
# resolves at runtime (used by FavoriteAppsService.getFavoriteApps).
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken