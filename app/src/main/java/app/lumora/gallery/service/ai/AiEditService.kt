package app.lumora.gallery.service.ai

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

class AiEditService {
    private val client = HttpClient(Android) { install(ContentNegotiation) { json() } }

    suspend fun testConnection(apiKey: String): Result<Unit> {
        return if (apiKey.isBlank()) Result.failure(IllegalArgumentException("API key is required")) else Result.success(Unit)
    }

    fun close() {
        client.close()
    }
}
