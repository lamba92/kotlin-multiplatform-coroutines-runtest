import org.gradle.internal.os.OperatingSystem
import Build_gradle.OS.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinCommonCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinOnlyTarget
import org.jetbrains.kotlin.konan.target.KonanTarget

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
version = "0.0.4-alpha"

kotlin {

    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
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




@Suppress("unused")
fun KotlinMultiplatformExtension.publish(vararg targets: Iterable<KotlinTarget>) =
    targets.toList().flatten()

@Suppress("unused")
fun KotlinMultiplatformExtension.publish(vararg metadata: KotlinCommonCompilation) =
    metadata.toList()

infix fun Iterable<KotlinTarget>.onlyOn(os: OS) = configure(this) {
    mavenPublication {
        tasks.withType<AbstractPublishToMaven>().all {
            onlyIf {
                publication != this@mavenPublication || when (os) {
                    LINUX -> OperatingSystem.current().isLinux
                    MAC -> OperatingSystem.current().isMacOsX
                    WINDOWS -> OperatingSystem.current().isWindows
                }
            }
        }
    }
}

enum class OS {
    LINUX, MAC, WINDOWS
}

val KotlinMultiplatformExtension.metadataPublication
    get() = targets.filterIsInstance<KotlinCommonCompilation>()

val KotlinMultiplatformExtension.nativeTargets
    get() = targets.filterIsInstance<KotlinNativeTarget>()

val KotlinMultiplatformExtension.platformIndependentTargets
    get() = targets.filter { it !is KotlinNativeTarget || it.konanTarget == KonanTarget.WASM32 }

val KotlinMultiplatformExtension.appleTargets
    get() = targets.filter {
        it is KotlinNativeTarget && listOf(
            KonanTarget.IOS_ARM64,
            KonanTarget.IOS_X64,
            KonanTarget.MACOS_X64,
            KonanTarget.IOS_ARM32
        ).any { target -> it.konanTarget == target }
    }

val KotlinMultiplatformExtension.windowsTargets
    get() = targets.filter {
        it is KotlinNativeTarget && listOf(
            KonanTarget.MINGW_X64,
            KonanTarget.MINGW_X86
        ).any { target -> it.konanTarget == target }
    }

val KotlinMultiplatformExtension.linuxTargets
    get() = targets.filter {
        it is KotlinNativeTarget && listOf(
            KonanTarget.LINUX_ARM32_HFP,
            KonanTarget.LINUX_MIPS32,
            KonanTarget.LINUX_MIPSEL32,
            KonanTarget.LINUX_X64
        ).any { target -> it.konanTarget == target }
    }

val KotlinMultiplatformExtension.androidTargets
    get() = targets.filter {
        it is KotlinNativeTarget && listOf(
            KonanTarget.ANDROID_ARM32,
            KonanTarget.ANDROID_ARM64
        ).any { target -> it.konanTarget == target }
    }