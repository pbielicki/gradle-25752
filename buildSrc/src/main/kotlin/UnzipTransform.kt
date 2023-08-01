import org.gradle.api.artifacts.transform.InputArtifact
import org.gradle.api.artifacts.transform.TransformAction
import org.gradle.api.artifacts.transform.TransformOutputs
import org.gradle.api.artifacts.transform.TransformParameters
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

@DisableCachingByDefault(because = "Not worth caching")
abstract class UnzipTransform : TransformAction<TransformParameters.None> {

    @get:[InputArtifact PathSensitive(PathSensitivity.NAME_ONLY)]
    abstract val zipFile: Provider<FileSystemLocation>

    @get:Inject
    abstract val archiveOperations: ArchiveOperations

    @get:Inject
    abstract val fs: FileSystemOperations

    override fun transform(outputs: TransformOutputs) {
        val zip = zipFile.get().asFile

        fs.sync {
            from(archiveOperations.zipTree(zipFile))
            into(outputs.dir(zip.nameWithoutExtension))
        }
    }
}