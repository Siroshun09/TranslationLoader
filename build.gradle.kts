plugins {
    `java-library`
    `maven-publish`
    signing

    id("io.freefair.aggregate-javadoc") version "6.4.1"
}

subprojects {
    apply {
        plugin<JavaPlugin>()
        plugin<JavaLibraryPlugin>()
        plugin<MavenPublishPlugin>()
        plugin<SigningPlugin>()
    }
}

val adventureVersion = "4.9.3"
val annotationsVersion = "23.0.0"

allprojects {
    val javaVersion = JavaVersion.VERSION_11

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly("net.kyori", "adventure-api", adventureVersion)
        compileOnly("org.jetbrains", "annotations", annotationsVersion)
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    }

    java {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
        withJavadocJar()
    }

    signing {
        useGpgCmd()
        sign(publishing.publications)
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])

                pom {
                    name.set("TranslationLoader")
                    description.set("A library that loads messages from a file or a directory.")
                    url.set("https://github.com/Siroshun09/TranslationLoader")

                    licenses {
                        license {
                            name.set("APACHE LICENSE, VERSION 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                            distribution.set("repo")
                        }
                    }

                    developers {
                        developer {
                            name.set("Siroshun09")
                        }
                    }

                    scm {
                        url.set("https://github.com/Siroshun09/TranslationLoader")
                        connection.set("scm:git:git://github.com/Siroshun09/TranslationLoader.git")
                        developerConnection.set("scm:git:ssh://github.com:Siroshun09/TranslationLoader.git")
                    }

                    issueManagement {
                        system.set("GitHub Issues")
                        url.set("https://github.com/Siroshun09/TranslationLoader/issues")
                    }

                    ciManagement {
                        system.set("GitHub Actions")
                        url.set("https://github.com/Siroshun09/TranslationLoader/actions")
                    }
                }
            }
        }

        repositories {
            maven {
                url = uri(
                    if (version.toString().endsWith("SNAPSHOT")) rootDir.resolve("staging-snapshot")
                    else rootDir.resolve("staging")
                )
            }
        }
    }

    tasks {
        compileJava {
            options.release.set(javaVersion.ordinal + 1)
        }

        javadoc {
            val opts = options as StandardJavadocDocletOptions

            opts.encoding = Charsets.UTF_8.name()
            opts.links(
                "https://docs.oracle.com/en/java/javase/${javaVersion.ordinal + 1}/docs/api/",
                "https://jd.adventure.kyori.net/api/$adventureVersion/",
                "https://javadoc.io/doc/org.jetbrains/annotations/$annotationsVersion/",
            )
        }

        test {
            useJUnitPlatform()
        }
    }
}

tasks {
    aggregateJavadoc {
        val opts = options as StandardJavadocDocletOptions

        opts.docTitle("TranslationLoader $version")
        opts.encoding = Charsets.UTF_8.name()
    }
}
