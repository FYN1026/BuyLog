# Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-keep class com.buylog.data.model.** { *; }

# Koin
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class com.buylog.**$$serializer { *; }
-keepclassmembers class com.buylog.** { *** Companion; }
-keepclasseswithmembers class com.buylog.** { kotlinx.serialization.KSerializer serializer(...); }

# Multiplatform settings
-keep class com.russhwolf.settings.** { *; }

# Coil
-dontwarn coil.**
-keep class coil.** { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }

# General
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.buylog.data.model.** { *; }
