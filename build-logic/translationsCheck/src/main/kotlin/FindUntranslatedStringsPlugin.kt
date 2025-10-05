
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

abstract class FindUntranslatedStringsTask : DefaultTask() {
    @TaskAction
    fun findUntranslatedStrings() {
        val resDir = File(project.projectDir, "src/main/res")
        val defaultStrings = File(resDir, "values/strings.xml")
        val defaultIdentities = getStringIdentitiesFromFile(defaultStrings)
        val translations: Map<String, List<String>> = findTranslations(resDir)

        val missingStrings = mutableMapOf<String, List<String>>()

        translations.forEach {
            val missing = defaultIdentities - it.value // находим отсутствующие идентификаторы строк
            if (missing.isNotEmpty()) {
                missingStrings[it.key] = missing // добавляем в карту отсутствующих строк
            }
        }

        if (missingStrings.isNotEmpty()) {
            throw GradleException(buildMissingStringsErrorMessage(missingStrings))
        }
    }

    private fun getStringIdentitiesFromFile(file: File): List<String> {
        // stringsFromXml будет содержать NodeList из values/strings.xml с тегом strings. Это список узлов дерева с именем string.
        val stringsFromXml = DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
            .parse(file)
            .getElementsByTagName("string")

        val stringIdentities =  stringsFromXml.let { nodeList ->
            (0 until nodeList.length).map { i ->
                val node = nodeList.item(i) // node - один узел из списка stringsFromXml.
                val name = node.attributes?.getNamedItem("name")?.nodeValue ?: "" // получаем атрибуты узла (в данном случае name).
                name // возвращаем значения атрибута name.
            }
        }

        return stringIdentities
    }

    private fun findTranslations(resDir: File): Map<String, List<String>> {
        return resDir.listFiles { file ->
            file.isDirectory && file.name.startsWith("values-") // получаем все папки с переводами
        }?.associate { dir ->
            val lang = dir.name.removePrefix("values-") // получаем код языка из имени папки
            val stringsFile = File(dir, "strings.xml") // получаем файл strings.xml в этой папке
            val identities = if (stringsFile.exists()) {
                getStringIdentitiesFromFile(stringsFile) // получаем список идентификаторов строк из файла перевода
            } else {
                emptyList()
            }
            lang to identities // возвращаем пару (код языка, список идентификаторов строк)
        } ?: emptyMap()
    }

    private fun buildMissingStringsErrorMessage(missingStrings: Map<String, List<String>>): String {
        val stringBuilderErrorText = StringBuilder("Missing translations").append(System.lineSeparator())
        missingStrings.forEach { missing ->
            stringBuilderErrorText
                .append("=== ${missing.key} ===")
                .append(System.lineSeparator())
                .append(missing.value.joinToString(separator = System.lineSeparator()))
                .append(System.lineSeparator())

        }
        return stringBuilderErrorText.toString()
    }
}

class FindUntranslatedStringsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("untranslatedStrings", FindUntranslatedStringsTask::class.java)
    }
}