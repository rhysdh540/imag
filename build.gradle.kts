plugins {
    `java-gradle-plugin`
}

java.toolchain.languageVersion = JavaLanguageVersion.of(8)

group = "dev.rdh"
version = "0.0"
base.archivesName = "imag"

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
}

gradlePlugin {
    plugins {
        register("imag") {
            id = "dev.rdh.imag"
            implementationClass = "dev.rdh.imag.ImagPlugin"
        }
    }
}
