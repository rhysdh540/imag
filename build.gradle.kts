plugins {
    `java-gradle-plugin`
}

group = "dev.rdh"
version = "0.0"
base.archivesName = "imag"

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
}
