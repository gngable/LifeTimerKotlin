# Add project-specific ProGuard rules here.
# For more details, see:
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep java.time classes (native at minSdk 26, no desugaring needed)
-keep class java.time.** { *; }

# Jetpack Compose — rules are bundled in the library AARs, nothing extra needed here.
