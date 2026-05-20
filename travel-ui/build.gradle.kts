plugins {
    base // Stellt Standard-Lifecycle-Tasks wie 'clean' und 'build' bereit
    id("com.github.node-gradle.node") version "7.0.2"
}

node {
    download.set(true)
    version.set("20.11.1")
    npmVersion.set("10.2.4")
}

// 1. Definiert den npm install Task
val npmInstallTask = tasks.named("npmInstall")

// 2. Definiert den Angular Build Task
val buildAngular = tasks.register<com.github.gradle.node.npm.task.NpmTask>("buildAngular") {
    dependsOn(npmInstallTask)

    // Führt im Hintergrund `npm run build` aus (aus der package.json)
    args.set(listOf("run", "build"))

    // Gradle Incremental Build: Führt den Build nur aus, wenn sich Dateien geändert haben!
    inputs.dir("src")
    inputs.file("package.json")
    inputs.file("angular.json")
    inputs.file("tsconfig.json")
    outputs.dir("dist")
}

// 3. Integriert den Angular Build in den normalen gradlew build Lebenszyklus
tasks.named("build") {
    dependsOn(buildAngular)
}

// 4. Clean-Task für den 'dist' Ordner konfigurieren
tasks.named<Delete>("clean") {
    delete("dist")
    delete(".angular") // Angular 17+ Cache Ordner
}