plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
    }
}

repositories {
    mavenCentral()
    maven(url = "https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.18.1-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.0")
}

project.extra.set("packageName", name.replace("-", ""))
project.extra.set("pluginName", name.split('-').joinToString("") { it.capitalize() })

tasks {
    processResources {
        filesMatching("**/*.yml") {
            expand(project.properties)
            expand(project.extra.properties)
        }
    }

    shadowJar {
        from(sourceSets["main"].output)
        archiveBaseName.set(project.extra.properties["pluginName"].toString())
        archiveVersion.set("") // For bukkit plugin update

        doLast {
            copy {
                from(archiveFile)
                val plugins = File(rootDir, ".debug/plugins/")
                into(if (File(plugins, archiveFileName.get()).exists()) File(plugins, "update") else plugins)
            }
        }
    }
}