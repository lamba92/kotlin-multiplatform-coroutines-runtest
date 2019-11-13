import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

plugins {
    id("org.jetbrains.kotlin.multiplatform") version "1.3.50"
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.4"
}

repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    mavenCentral()
    jcenter()
}

group = "com.github.lamba92"
version = System.getenv("TRAVIS_TAG") ?: "0.0.1"

kotlin {

    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    js()
    mingwX64("windows-x64")
    iosArm32("ios-arm32")
    iosArm64("ios-arm64")
    macosX64("macos-x64")
    linuxX64("linux-x64")

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

        val `windows-x64Main` by getting {
            dependsOn(jvmAndNativeCommon)
            dependencies {
                implementation(coroutines("core-windowsx64", coroutinesVersion))
            }
        }

        val `ios-arm32Main` by getting {
            dependsOn(jvmAndNativeCommon)
            dependencies {
                implementation(coroutines("core-iosarm32", coroutinesVersion))
            }
        }

        val `macos-x64Main` by getting {
            dependsOn(jvmAndNativeCommon)
            dependencies {
                implementation(coroutines("core-macosx64", coroutinesVersion))
            }
        }

        val `ios-arm64Main` by getting {
            dependsOn(jvmAndNativeCommon)
            dependencies {
                implementation(coroutines("core-iosarm64", coroutinesVersion))
            }
        }

        val `linux-x64Main` by getting {
            dependsOn(jvmAndNativeCommon)
            dependencies {
                implementation(coroutines("core-linuxx64", coroutinesVersion))
            }
        }

    }
}

publishing {
    (publications["kotlinMultiplatform"] as MavenPublication).artifactId =
        "${rootProject.name}-${project.name}-common"
}

bintray {
    user = searchPropertyOrNull("bintrayUsername")
    key = searchPropertyOrNull("bintrayApiKey")
    pkg {
        version {
            name = project.version.toString()
        }
        repo = "com.github.lamba92"
        name = "kotlin-multiplatform-coroutines-runtest"
        setLicenses("Apache-2.0")
        vcsUrl = "https://github.com/lamba92/kotlin-multiplatform-coroutines-runtest"
        issueTrackerUrl = "https://github.com/lamba92/kotlin-multiplatform-coroutines-runtest/issues"
    }
    publish = true

    if (OperatingSystem.current().isMacOsX)
        setPublications("jvm", "js", "macos-x64", "ios-arm64", "ios-arm32", "linux-x64")
    else
        setPublications("windows-x64")
}

extensions.findByName("buildScan")?.withGroovyBuilder {
    setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
    setProperty("termsOfServiceAgree", "yes")
}

fun searchPropertyOrNull(propertyName: String): String? =
    project.findProperty(propertyName) as String? ?: System.getenv(propertyName)

fun BintrayExtension.pkg(action: BintrayExtension.PackageConfig.() -> Unit) {
    pkg(closureOf(action))
}

fun BintrayExtension.PackageConfig.version(action: BintrayExtension.VersionConfig.() -> Unit) {
    version(closureOf(action))
}

@Suppress("unused")
fun KotlinDependencyHandler.kotlinx(module: String, version: String? = null) =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" } ?: ""}"

@Suppress("unused")
fun KotlinDependencyHandler.coroutines(module: String, version: String? = null) =
    kotlinx("coroutines-$module", version)
