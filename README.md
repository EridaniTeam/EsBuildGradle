# Esbuild Gradle

## What is this?
use esbuild(https://esbuild.github.io/) to make js bundle for Kotlin/Js

Webpack is way toooo slow

based on (https://github.com/soywiz/kotlinjs-esbuild)
make it a plugin easier to use.

## Before using it
Before using this plugin, please make sure read esbuild document (https://esbuild.github.io/) first.

Might be useful if something inside this plugin makes you confused
1. https://esbuild.github.io/api/#loader
2. https://esbuild.github.io/api/#external

## Apply the plugin
```kotlin
// build.gradle.kts
plugins {
    // other plugins
    // it will be on gradle plugin portal soon
    id("club.eridani.esbuild-gradle") version "0.0.1"
}
```


## Example usage
```kotlin
kotlin {
    // other targets or pure js
    
    js(IR) {
        binaries.executable()
        browser {
            useCommonJs()

            commonWebpackConfig {
                cssSupport.enabled = false
            }

            useEsbuild {
                externals += "react"
                externals += "react-dom"
                externals += "styled-components"
                externals += "@material-ui/*"
                externals += "@jetbrains/*"
            }
        }
    }
}
```

## Custom esbuild version
```kotlin
useEsbuild {
    // you don't need to define this, if you don't define it, it will use the default one
    esbuildVersion = "0.12.28"
    // other stuff
}
```
