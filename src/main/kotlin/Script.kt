import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.reflect.TypeToken

data class Script(
  val id: String,
  val logo: String? = null,
  val name: String? = null,
  val author: String? = null,
) {
  companion object {
    fun getRolesOnScript(gson: Gson, json: String): Set<String> =
      gson.fromJson<List<JsonElement>>(json, object : TypeToken<List<JsonElement>>() {}.type)
        .mapNotNull { parseRole(it) }.toSet()
        .plus(listOf("minioninfo", "demoninfo", "dusk", "dawn"))

    private fun parseRole(entry: JsonElement): String? =
      when (entry) {
        is JsonObject -> parseRole(gson.fromJson(entry, Script::class.java))
        is JsonPrimitive -> parseRole(gson.fromJson(entry, String::class.java))
        else -> null

      }

    private fun parseRole(entry: Script): String? =
      if (entry.id == "_meta") null else parseRole(entry.id)
    private fun parseRole(entry: String): String = entry.lowercase().replace(Regex("[^a-z]"), "")

    fun getScriptMetadata(gson: Gson, json: String): Script? =
      gson.fromJson<List<JsonElement>?>(json, object : TypeToken<List<JsonElement>>() {}.type)
        .filter { it.isJsonObject }.map { gson.fromJson(it, Script::class.java) }
        .firstOrNull { it.id == "_meta" }
  }
}