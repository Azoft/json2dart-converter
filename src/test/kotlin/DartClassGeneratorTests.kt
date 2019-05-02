import com.azoft.json2dart.delegates.generator.DartClassGenerator
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ideals.inner.InnerResponse
import ideals.list.ListResponse
import ideals.simple.SimpleResponse
import org.junit.After
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

    @After
    fun after() {
        clearDir(dartPodoPath)
        clearDir(jsonGeneratedPath)
    }

    @Test
    fun testSimpleClassGenerating() {
        generateAndValidate<SimpleResponse>(
            "$jsonInitialPath/simple_initial.json",
            "$jsonGeneratedPath/simple_initial.json"
        ) {
            assert(it.doubleField == 1.0)
            assert(it.intField == 1)
            assert(it.stringField == "string")
            assert(it.objectField == null)
        }
    }

    @Test
    fun testListResponse() {
        generateAndValidate<ListResponse>(
            "$jsonInitialPath/list_initial.json",
            "$jsonGeneratedPath/list_initial.json"
        ) {
            assert(it.intList == listOf(0, 1, 2))
            assert(it.stringList == listOf("a", "b", "c"))
            assert(it.doubleList == listOf(0.0, 1.0, 2.0))
            assert(it.objectList?.isEmpty() ?: false)
            assert(it.classList?.size == 1)
            assert(
                it.classList?.get(0)?.run {
                    doubleField == 1.0
                        && intField == 1
                        && stringField == "string"
                        && objectField == null
                } ?: false
            )
        }
    }

    @Test
    fun testInnerResponse() {
        generateAndValidate<InnerResponse>(
            "$jsonInitialPath/inner_object_initial.json",
            "$jsonGeneratedPath/inner_object_generated.json"
        ) {
            assert(it.inner != null)
            it.inner?.let { inner ->
                assert(inner.doubleField == 1.0)
                assert(inner.intField == 1)
                assert(inner.stringField == "string")
                assert(inner.objectField == null)
                assert(it.inner.secondInner != null)
                it.inner.secondInner?.let { secondInner ->
                    assert(secondInner.doubleField == 1.0)
                    assert(secondInner.intField == 1)
                    assert(secondInner.stringField == "string")
                    assert(secondInner.objectField == null)
                }
            }
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