plugins {
    base
}

repositories {
    mavenCentral()
}

val apacheMaven: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
    resolutionStrategy.cacheDynamicVersionsFor(1, TimeUnit.HOURS)
    attributes.attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, ArtifactTypeDefinition.DIRECTORY_TYPE)
}

dependencies {
    registerTransform(UnzipTransform::class) {
        from.attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, ArtifactTypeDefinition.ZIP_TYPE)
        to.attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, ArtifactTypeDefinition.DIRECTORY_TYPE)
    }

    apacheMaven("org.apache.maven:apache-maven:3.9.3") {
        artifact {
            classifier = "bin"
            type = "zip"
            isTransitive = false
        }
    }
}

tasks.register("installMaven", InstallMaven::class) {
    dependsOn(apacheMaven)
    output.convention(layout.buildDirectory.file("maven.txt"))
    installationDirectory.convention(layout.dir(provider { apacheMaven.singleFile }).map { it.dir("apache-maven-3.9.3") })
}

@CacheableTask
abstract class InstallMaven : DefaultTask() {
    @get:OutputFile
    abstract val output: RegularFileProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val installationDirectory: DirectoryProperty

    @TaskAction
    fun printMavenFiles() {
        with(output.get().asFile) {
            appendText("Contents of ${installationDirectory.asFile.get().absolutePath}:\n")
            installationDirectory.asFile.get().listFiles()?.map { "${it.absolutePath}\n" }?.forEach(::appendText)
        }
    }
}
