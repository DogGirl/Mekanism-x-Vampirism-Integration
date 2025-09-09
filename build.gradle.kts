import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("eclipse")
    id("idea")
    id("net.minecraftforge.gradle") version "[6.0,6.2)"
    id("org.parchmentmc.librarian.forgegradle") version "1.+"
    id("org.spongepowered.mixin") version "0.7.+"

}

val modVersion: String by extra
val forgeVersion: String by extra
val parchmentMappingsVersion: String by extra
val minecraftVersion: String by extra
val forgeVersionRange: String by extra
val loaderVersionRange: String by extra
val mekanismVersion: String by extra
val mekanismVersionRange: String by extra
val curiosVersion: String by extra
val curiosVersionRange: String by extra


version = "$minecraftVersion-$modVersion"
group = "com.msdoggirl.mekvamp"

base {
    archivesName = "mekvamp"
}

java.toolchain.languageVersion = JavaLanguageVersion.of(17)

minecraft {
    mappings("parchment", "$parchmentMappingsVersion-$minecraftVersion")

    copyIdeResources = true

    accessTransformers.setFrom("src/main/resources/META-INF/accesstransformer.cfg")
    
    runs {
        create("client") {
            property("forge.enabledGameTestNamespaces", "mekanismcurios")
        }

        create("server") {
            property("forge.enabledGameTestNamespaces", "mekanismcurios")
            args("--nogui")
        }
        
        create("data") {
            workingDirectory(project.file("run-data"))
            args("--mod", "mekanismcurios", "--all", "--output", file("src/generated/resources/").absolutePath, "--existing", file("src/main/resources/").absolutePath)
        }

        configureEach {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${projectDir}/build/createSrgToMcp/output.srg")

            mods {
                create("mekanismcurios") {
                    source(sourceSets.main.get())
                }
            }
        }
    }
}

sourceSets.main.get().resources { srcDir("src/generated/resources") }


repositories {
    maven("https://modmaven.dev/")
    maven("https://maven.theillusivec4.top/")
    maven("https://www.cursemaven.com")
    maven("https://maven.minecraftforge.net")
    maven("https://maven.maxanier.de/releases")
}

dependencies {
    minecraft("net.minecraftforge:forge:${minecraftVersion}-${forgeVersion}")
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    compileOnly(fg.deobf("de.teamlapen.vampirism:Vampirism:1.20.1-1.10.13:api"))
    runtimeOnly(fg.deobf("de.teamlapen.vampirism:Vampirism:1.20.1-1.10.13:api"))
    compileOnly(fg.deobf("de.teamlapen.vampirism:Vampirism:1.20.1-1.10.13"))
    runtimeOnly(fg.deobf("de.teamlapen.vampirism:Vampirism:1.20.1-1.10.13"))
    compileOnly(fg.deobf("mekanism:Mekanism:1.20.1-10.4.16.80"))
    runtimeOnly(fg.deobf("mekanism:Mekanism:1.20.1-10.4.16.80"))
    runtimeOnly(fg.deobf("curse.maven:emi-580555:6420945"))

}

tasks.processResources {
    var replaceProperties = mapOf("modVersion" to modVersion, "loaderVersionRange" to loaderVersionRange,
        "forgeVersionRange" to forgeVersionRange, "minecraftVersion" to minecraftVersion,
        "mekanismVersionRange" to mekanismVersionRange, "curiosVersionRange" to curiosVersionRange)

    inputs.properties(replaceProperties)
    filesMatching("META-INF/mods.toml") {
        expand(replaceProperties)
    }
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            mapOf(
                "Specification-Title"     to "mekvamp", 
                "Specification-Vendor"    to "MsDogGirl", 
                "Specification-Version"   to "1", 
                "Implementation-Title"    to "Mekanism x Vampirism", 
                "Implementation-Version"  to project.tasks.jar.get().archiveVersion.get(), 
                "Implementation-Timestamp" to ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")),
        
            )
        )
    }

    finalizedBy("reobfJar")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
