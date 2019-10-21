import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

plugins {
    id("org.jetbrains.kotlin.multiplatform") version "1.3.50"
    `maven-publish`
}
repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    mavenCentral()
    jcenter()
}
group = "com.github.lamba92"
version = "0.0.1"

kotlin {
    jvm()
    js()
    mingwX64()
    iosArm32()
    iosArm64()
    macosX64()
    linuxX64()

    sourceSets {
        val coroutinesVersion = "1.3.2"

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(coroutines("core-common", coroutinesVersion))
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(coroutines("core-js", coroutinesVersion))
            }
        }

        val jvmAndNativeCommon = create("jvmAndNativeCommonMain")

        val jvmMain by getting {
            dependsOn(jvmAndNativeCommon)
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation(coroutines("core", coroutinesVersion))

            }
        }

        val mingwX64Main by getting {
            dependsOn(jvmAndNativeCommon)
            dependencies {
                implementation(coroutines("core-windowsx64", coroutinesVersion))
            }
        }

        val iosArm32Main by getting {
            dependsOn(jvmAndNativeCommon)
            dependencies {
                implementation(coroutines("core-iosarm32", coroutinesVersion))
            }
        }

        val macosX64Main by getting {
            dependsOn(jvmAndNativeCommon)
            dependencies {
                implementation(coroutines("core-macosx64", coroutinesVersion))
            }
        }

        val iosArm64Main by getting {
            dependsOn(jvmAndNativeCommon)
            dependencies {
                implementation(coroutines("core-iosarm64", coroutinesVersion))
            }
        }

        val linuxX64Main by getting {
            dependsOn(jvmAndNativeCommon)
            dependencies {
                implementation(coroutines("core-linuxx64", coroutinesVersion))
            }
        }

    }
}

fun property(propertyName: String): String? =
    project.findProperty(propertyName) as String? ?: System.getenv(propertyName)

publishing {
    repositories {
        maven("https://maven.pkg.github.com/${property("githubAccount")}/${rootProject.name}") {
            name = "GitHubPackages"
            credentials {
                username = property("githubAccount")
                password = property("githubToken")
            }
        }
    }
}

@Suppress("unused")
fun KotlinDependencyHandler.kotlinx(module: String, version: String? = null) =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" } ?: ""}"

@Suppress("unused")
fun KotlinDependencyHandler.coroutines(module: String, version: String? = null) =
    kotlinx("coroutines-$module", version)