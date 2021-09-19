package club.eridani.esbuild

import club.eridani.esbuild.dsl.EsbuildDsl
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import java.io.File

class EsbuildGradlePlugin : Plugin<Project> {

    companion object {
        /**
         * This is not good I guess, maybe failed in subproject
         * gradle make subproject hierarchy piece of sh*t
         * time to make a build system? lol
         **/
        lateinit var project: Project
        lateinit var dsl: EsbuildDsl
        lateinit var esbuildFolder: File
        lateinit var esbuildCmd: File

        val env by lazy { NodeJsRootPlugin.apply(project.rootProject).requireConfigured() }

        val npmCmd by lazy { File(env.nodeDir, if (env.isWindows) "npm.cmd" else "npm") }
    }

    override fun apply(target: Project) {
        project = target
    }
}

fun Project.init() {

    with(EsbuildGradlePlugin.Companion) {

        val kotlin = project.extensions.getByName("kotlin") as org.jetbrains.kotlin.gradle.dsl.KotlinJsProjectExtension
        dsl = project.extensions.create("esbuild", EsbuildDsl::class.java)
        esbuildFolder = File(rootProject.buildDir, "esbuild")


        esbuildCmd = when (hostOs) {
            OS.Windows -> File(esbuildFolder, "esbuild.cmd")
            else -> File(esbuildFolder, "esbuild")
        }

        val installEsbuild = tasks.create("installEsbuild", Exec::class.java) {
            with(it) {
                group = "esbuild"
                onlyIf { !esbuildCmd.exists() }
                val esbuildVersion = dsl.esbuildVersion
                afterEvaluate {
                    commandLine(npmCmd,
                        "-g",
                        "install",
                        "esbuild@$esbuildVersion",
                        "--prefix",
                        esbuildFolder.absolutePath)
                }
            }
        }



        for (debug in listOf(true, false)) {
            val debugPrefix = if (debug) "Debug" else "Release"
            val productionInfix = if (debug) "Development" else "Production"

            val outFolder = File(buildDir, if (debug) "developmentExecutable" else "distributions")
            val resources = tasks.create("browser${productionInfix}EsbuildResources", Copy::class.java) {
                with(it) {
                    group = "esbuild"
                    for (sourceSet in kotlin.js().compilations.flatMap { it.kotlinSourceSets }) {
                        from(sourceSet.resources)
                    }

                    into(outFolder)
                }
            }

            val prepare = tasks.create("browserPrepareEsbuild$debugPrefix") {
                with(it) {
                    group = "esbuild"
                    dependsOn("compile${productionInfix}ExecutableKotlinJs")
                    dependsOn(resources)
                    dependsOn(installEsbuild)
                }
            }

            tasks.create("browser${debugPrefix}Esbuild", Exec::class.java) { t ->
                with(t) {
                    group = "esbuild"
                    dependsOn(prepare)
                    val jsPath =
                        tasks.getByName("compile${productionInfix}ExecutableKotlinJs").outputs.files.first { it.extension.toLowerCase() == "js" }

                    afterEvaluate {

                        val path = when (hostOs) {
                            OS.Windows -> "\"${environment["PATH"]}\";${env.nodeDir}"
                            else -> "\"${environment["PATH"]}\":${env.nodeDir}"
                        }
                        environment("PATH", path)

                        val cmd = mutableListOf(
                            esbuildCmd.absolutePath,
                            jsPath.absolutePath,
                            "--outfile=${File(outFolder, "${project.name}.js")}",
                            "--bundle",
                            "--minify",
                            "--sourcemap=external"
                        )

                        dsl.externals.forEach {
                            cmd.add("--external:$it")
                        }

                        commandLine(cmd)
                    }

                }
            }

        }


    }
}