val cargo = "${System.getProperty("user.home")}/.cargo/bin/cargo"

tasks.create("build") {
    group = "build"
    doLast {
        exec {
            commandLine(cargo, "build", "--lib", "--verbose", "--color", "always")
        }.rethrowFailure()
    }
}

tasks.create("clean") {
    group = "build"
    doLast {
        exec {
            commandLine(cargo, "clean", "--verbose", "--color", "always")
        }.rethrowFailure()
    }
}