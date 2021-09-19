package club.eridani.esbuild.dsl

import club.eridani.esbuild.EsbuildGradlePlugin
import club.eridani.esbuild.init
import org.gradle.api.Action
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBrowserDsl

abstract class EsbuildDsl : ExtensionAware {
    var esbuildVersion: String = "0.12.28"

    val externals = mutableListOf<String>()

}

/**
 * hacky code to use our own dsl in kotlin dsl
 * it is cool
 */
fun KotlinJsBrowserDsl.useEsbuild(builder: Action<EsbuildDsl>? = null) {
    EsbuildGradlePlugin.project.init()
    builder?.let {
        EsbuildGradlePlugin.project.extensions.configure("esbuild", it)
    }
}