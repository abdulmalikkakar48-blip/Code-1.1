import java.net.URI
import java.util.Base64

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Identifiants publics du projet seulement. La clé OpenAI reste sur le serveur.
fun coachConfig(name: String, variable: String): String = providers.gradleProperty(name)
    .orElse(providers.environmentVariable(variable)).orElse("").get()
fun quoted(value: String): String = "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"")
    .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t") + "\""

val coachUrl = coachConfig("selkieSupabaseUrl", "SELKIE_SUPABASE_URL")
val coachPublicKey = coachConfig("selkieSupabasePublicKey", "SELKIE_SUPABASE_PUBLIC_KEY")
val isPublicCoachKey = coachPublicKey.matches(Regex("sb_publishable_[A-Za-z0-9_-]{8,}")) || runCatching {
    val parts = coachPublicKey.split('.')
    require(parts.size == 3)
    val payload = String(Base64.getUrlDecoder().decode(parts[1]), Charsets.UTF_8)
    (groovy.json.JsonSlurper().parseText(payload) as? Map<*, *>)?.get("role") == "anon"
}.getOrDefault(false)
require(coachPublicKey.isEmpty() || isPublicCoachKey) {
    "Coach : seule une clé publique publishable ou anon peut être intégrée à Android. Clés privées interdites."
}
require(coachUrl.isEmpty() || runCatching {
    val url = URI(coachUrl)
    url.scheme == "https" && url.host?.endsWith(".supabase.co") == true && url.userInfo == null &&
        url.port == -1 && url.query == null && url.fragment == null && url.path in listOf("", "/")
}.getOrDefault(false)) { "Coach : fournir uniquement l’URL HTTPS publique du projet Supabase." }
require(coachUrl.isEmpty() == coachPublicKey.isEmpty()) {
    "Coach : configurer ensemble l’URL du projet et sa clé publique, ou laisser les deux vides."
}

android {
    namespace = "com.maree.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.maree.app"
        minSdk = 29
        targetSdk = 34
        versionCode = 13
        versionName = "0.9.2"
        buildConfigField("String", "SUPABASE_URL", quoted(coachUrl))
        buildConfigField("String", "SUPABASE_PUBLIC_KEY", quoted(coachPublicKey))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures { compose = true; buildConfig = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.14" }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.json:json:20240303")
}
