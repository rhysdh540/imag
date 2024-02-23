plugins {
    application
    cpp
}

group = "dev.rdh"
version = "0.1"
base.archivesName = "imag"

val mainClassName = "dev.rdh.imag.ImagMain"

application {
    mainClass = mainClassName
    applicationDefaultJvmArgs = listOf("-Djava.library.path=build/libs")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = mainClassName
    }
}
