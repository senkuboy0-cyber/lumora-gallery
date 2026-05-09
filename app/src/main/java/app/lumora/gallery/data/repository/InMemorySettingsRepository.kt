package app.lumora.gallery.data.repository

import app.lumora.gallery.domain.model.AppSettings
import app.lumora.gallery.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class InMemorySettingsRepository : SettingsRepository {
    private val state = MutableStateFlow(AppSettings())
    private var apiKey: String = ""

    override val settings: Flow<AppSettings> = state

    override suspend fun setFirstLaunchComplete(value: Boolean) {
        state.value = state.value.copy(firstLaunchComplete = value)
    }

    override suspend fun setAiModelDownloaded(value: Boolean) {
        state.value = state.value.copy(aiModelDownloaded = value)
    }

    override suspend fun setGridColumns(value: Int) {
        state.value = state.value.copy(gridColumns = value.coerceIn(2, 4))
    }

    override suspend fun saveEncryptedApiKey(value: String) {
        apiKey = value
    }

    override suspend fun readEncryptedApiKey(): String = apiKey
}
