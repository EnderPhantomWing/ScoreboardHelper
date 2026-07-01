plugins {
    id("maven-publish")
    id("com.github.hierynomus.license") version "0.16.1" apply false
    id("net.fabricmc.fabric-loom") version "1.17.12" apply false
    id("net.fabricmc.fabric-loom-remap") version "1.17.12" apply false
    id("com.replaymod.preprocess") version "c5abb4fb12"
}

preprocess {
    strictExtraMappings = false

    val mc12006     = createNode("1.20.6",  1_20_06,    "official")

    mc12006     .link(  mc12006,    null)

    // See https://github.com/Fallen-Breath/fabric-mod-template/blob/1d72d77a1c5ce0bf060c2501270298a12adab679/build.gradle#L55-L63
    for (node in getNodes()) {
        findProject(node.project)
            ?.ext
            ?.set("mcVersion", node.mcVersion)
    }
}

tasks.register("buildAndGather") {
    subprojects {
        dependsOn(tasks.named("build"))
    }
    doFirst {
        println("Gathering builds")
        val buildLibs = { p: Project -> p.layout.buildDirectory.dir("libs").get().asFile.toPath() }
        delete(fileTree(buildLibs(rootProject)) { include("*") })
        subprojects {
            copy {
                from(buildLibs(project)) {
                    include("*.jar")
                    exclude("*-dev.jar", "*-sources.jar", "*-shadow.jar")
                }
                into(buildLibs(rootProject))
                duplicatesStrategy = DuplicatesStrategy.INCLUDE
            }
        }
    }
}