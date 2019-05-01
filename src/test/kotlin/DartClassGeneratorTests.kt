import com.azoft.json2dart.delegates.generator.DartClassGenerator
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ideals.simple.Response
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class DartClassGeneratorTests {

    private val dartPodoPath = "src/test/dart/lib"
    private val jsonGeneratedPath = "src/test/resources/generated"
    private val jsonInitialPath = "src/test/resources/start"
    private val dartExecutable = "src/test/dart/bin/main.dart"

    @Before
    fun before() {
        clearDir(dartPodoPath)
        clearDir(jsonGeneratedPath)
    }

    @Test
    fun testSimpleClassGenerating() {
        generateAndValidate<Response>(
            "$jsonInitialPath/simple_initial.json",
            "$jsonGeneratedPath/simple_initial.json"
        ) {
            assert(it.doubleField == 1.0)
            assert(it.intField == 1)
            assert(it.stringField == "string")
            assert(it.objectField == null)
        }
    }

    private inline fun <reified T> generateAndValidate(
        initialPath: String,
        generatedPath: String,
        validate: ((res: T) -> Unit)
    ) {
        DartClassGenerator()
            .generateFromJson(
                File(initialPath).readText(),
                File(dartPodoPath),
                "response",
                false
            )

        print("dart $dartExecutable $initialPath $generatedPath".runCommand())

        validate(jacksonObjectMapper().readValue(File(generatedPath).readText(), T::class.java))
    }

    private fun String.runCommand(workingDir: File? = null): String? =
        try {
            val parts = this.split("\\s".toRegex())
            val proc = ProcessBuilder(*parts.toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            proc.waitFor(60, TimeUnit.MINUTES)
            proc.inputStream.bufferedReader().readText()
        } catch(e: IOException) {
            e.printStackTrace()
            null
        }

    private fun clearDir(path: String) =
        File(path).takeIf { it.isDirectory }?.listFiles()?.forEach {
            it.deleteRecursively()
        }
}