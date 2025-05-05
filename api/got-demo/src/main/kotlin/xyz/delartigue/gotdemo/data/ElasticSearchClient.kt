package xyz.delartigue.gotdemo.data

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ElasticSearchClient(
    @Value("\${elasticsearch.host}")
    private val host: String,
) {
    val URI = "/got/_search"
    val client = okhttp3.OkHttpClient()

    fun query(query: String): Response {
        val json = """
            {"query":{"bool":{"should":[{"match":{"actor_name":"$query"}},{"match":{"character_name":"$query"}}]}}}
        """.trimIndent()

        val body = json.toRequestBody("application/json".toMediaTypeOrNull())

        val request = okhttp3.Request.Builder()
            .url(host + URI)
            .post(body)
            .build()

        return client.newCall(request).execute()
    }
}