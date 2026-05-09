package app.lumora.gallery.domain.repository

import app.lumora.gallery.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun setFirstLaunchComplete(value: Boolean)
    suspend fun setAiModelDownloaded(value: Boolean)
    suspend fun setGridColumns(value: Int)
    suspend fun saveEncryptedApiKey(value: String)
    suspend fun readEncryptedApiKey(): String
}
