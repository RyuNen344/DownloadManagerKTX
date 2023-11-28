plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "io.github.ryunen344.donwload.manager.ktx"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            pom {
                name.set("DownloadManagerKTX")
                description.set("Allows you to use Tink Primitive Encryption in your Kotlin Multiplatform Mobile project")
                url.set("https://github.com/RyuNen344/DownloadManagerKTX")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://github.com/RyuNen344/DownloadManagerKTX/blob/main/LICENSE")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("RyuNen344")
                        name.set("Bunjiro Miyoshi")
                        email.set("s1100633@outlook.com")
                    }
                }
                scm {
                    connection.set("scm:git://github.com/RyuNen344/DownloadManagerKTX.git")
                    developerConnection.set("scm:git://github.com/RyuNen344/DownloadManagerKTX.git")
                    url.set("https://github.com/RyuNen344/DownloadManagerKTX")
                }
            }

            afterEvaluate {
                from(components["release"])
            }
        }
    }

    repositories {
        maven {
            name = "Local"
            url = uri(rootProject.layout.projectDirectory.dir("releases"))
        }
    }
}
