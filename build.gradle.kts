buildscript {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    checkstyle
    java
    id("com.simonharrer.modernizer") version "2.1.0-1" apply false
    id("com.github.ben-manes.versions") version "0.36.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.15"
}

apply<BootstrapPlugin>()
apply<VersionPlugin>()

allprojects {
    group = "dr.manhattan"
    apply<MavenPublishPlugin>()
}

allprojects {
    apply<MavenPublishPlugin>()

    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()
    }
}

subprojects {
    group = "dr.manhattan.externals"

    project.extra["PluginProvider"] = "Manhattan"
    project.extra["ProjectUrl"] = "https://discord.gg/sT72mqJjvx"
    project.extra["PluginLicense"] = "3-Clause BSD License"

    repositories {
        mavenCentral {
            content {
                excludeGroupByRegex("com\\.openosrs.*")
            }
        }

        jcenter {
            content {
                excludeGroupByRegex("com\\.openosrs.*")
            }
        }

        exclusiveContent {
            forRepository {
                mavenLocal()
            }
            filter {
                includeGroupByRegex("com\\.openosrs.*")
                includeGroupByRegex("dr\\.manhattan.*")

            }
        }
    }

    apply<JavaPlugin>()
    apply(plugin = "checkstyle")
    apply(plugin = "com.simonharrer.modernizer")

    dependencies {
        compileOnly(group = "com.openosrs", name = "http-api", version = "3.5.4")
        compileOnly(group = "com.openosrs", name = "runelite-api", version = "3.5.4")
        compileOnly(group = "com.openosrs", name = "runelite-client", version = "3.5.4")

        compileOnly(group = "org.apache.commons", name = "commons-text", version = "1.9")
        compileOnly(group = "com.google.inject", name = "guice", version = "4.2.3", classifier = "no_aop")
        compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.16")
        compileOnly(group = "org.pf4j", name = "pf4j", version = "3.5.0")
        compileOnly(group = "io.reactivex.rxjava3", name = "rxjava", version = "3.0.6")

    }

    checkstyle {
        maxWarnings = 0
        toolVersion = "8.25"
        isShowViolations = true
        isIgnoreFailures = false
    }

    configure<PublishingExtension> {
        repositories {
            maven {
                url = uri("$buildDir/repo")
            }
        }
        publications {
            register("mavenJava", MavenPublication::class) {
                from(components["java"])
            }
        }
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }


        withType<AbstractArchiveTask> {
            isPreserveFileTimestamps = false
            isReproducibleFileOrder = true
            dirMode = 493
            fileMode = 420
        }

        withType<Checkstyle> {
            group = "verification"
        }

        register<Copy>("copyDeps") {
            into("./build/deps/")
            from(configurations["runtimeClasspath"])
        }
    }
}


fun isNonStable(version: String): Boolean {
    return listOf("ALPHA", "BETA", "RC").any {
        version.toUpperCase().contains(it)
    }
}
