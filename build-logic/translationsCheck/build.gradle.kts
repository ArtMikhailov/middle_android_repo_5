import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}


java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}


kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

gradlePlugin {
    plugins {
        register("build-logic") {
            id = libs.plugins.build.logic.check.translations.get().pluginId
            implementationClass = "FindUntranslatedStringsPlugin"
        }
    }
}

