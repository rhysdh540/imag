plugins {
    java
}

allprojects {
    group = "dev.rdh"
    version = "0.1"
}

base.archivesName = "imag"

val mainClassName = "dev.rdh.imag.ImagMain"

tasks.compileJava {
    dependsOn(project(":rust").tasks.build)
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = mainClassName
    }
}
