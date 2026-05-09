package app.lumora.gallery.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lumora.gallery.data.repository.InMemoryMediaRepository
import app.lumora.gallery.data.repository.InMemorySettingsRepository
import app.lumora.gallery.domain.model.Album
import app.lumora.gallery.domain.model.AppSettings
import app.lumora.gallery.domain.model.MediaItem
import app.lumora.gallery.domain.repository.MediaRepository
import app.lumora.gallery.domain.repository.SettingsRepository
import app.lumora.gallery.domain.usecase.GenerateShareCodeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LumoraUiState(
    val settings: AppSettings = AppSettings(),
    val media: List<MediaItem> = emptyList(),
    val albums: List<Album> = emptyList(),
    val selectedTab: HomeTab = HomeTab.Photos,
    val currentScreen: LumoraScreen = LumoraScreen.Home,
    val selectedMedia: MediaItem? = null,
    val selectedIds: Set<Long> = emptySet(),
    val shareCode: String = "",
    val message: String? = null
)

enum class HomeTab { Photos, Albums }
enum class LumoraScreen { Home, Search, Viewer, Editor, Share, Locker, RecycleBin, Settings, StorageInfo }

class LumoraViewModel(
    private val mediaRepository: MediaRepository = InMemoryMediaRepository(),
    private val settingsRepository: SettingsRepository = InMemorySettingsRepository(),
    private val generateShareCode: GenerateShareCodeUseCase = GenerateShareCodeUseCase()
) : ViewModel() {
    private val navigation = MutableStateFlow(LumoraUiState())

    val uiState: StateFlow<LumoraUiState> = combine(
        navigation,
        mediaRepository.observeMedia(),
        mediaRepository.observeAlbums(),
        settingsRepository.settings
    ) { nav, media, albums, settings ->
        nav.copy(media = media, albums = albums, settings = settings)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LumoraUiState())

    fun completeOnboarding() = viewModelScope.launch { settingsRepository.setFirstLaunchComplete(true) }
    fun downloadAiModel() = viewModelScope.launch { settingsRepository.setAiModelDownloaded(true); showMessage("AI features enabled!") }
    fun skipAiModel() = viewModelScope.launch { settingsRepository.setFirstLaunchComplete(true) }
    fun selectTab(tab: HomeTab) { navigation.value = navigation.value.copy(selectedTab = tab) }
    fun open(screen: LumoraScreen) { navigation.value = navigation.value.copy(currentScreen = screen, selectedIds = emptySet()) }
    fun openViewer(item: MediaItem) { navigation.value = navigation.value.copy(currentScreen = LumoraScreen.Viewer, selectedMedia = item) }
    fun backHome() { navigation.value = navigation.value.copy(currentScreen = LumoraScreen.Home, selectedMedia = null, selectedIds = emptySet()) }
    fun toggleSelect(id: Long) { navigation.value = navigation.value.copy(selectedIds = navigation.value.selectedIds.toggle(id)) }
    fun toggleFavourite(id: Long) = viewModelScope.launch { mediaRepository.toggleFavourite(id) }
    fun deleteSelected() = viewModelScope.launch { mediaRepository.moveToRecycleBin(uiState.value.selectedIds); backHome() }
    fun generateRemoteCode() { navigation.value = navigation.value.copy(shareCode = generateShareCode()) }
    fun clearMessage() { navigation.value = navigation.value.copy(message = null) }
    private fun showMessage(value: String) { navigation.value = navigation.value.copy(message = value) }
}

private fun Set<Long>.toggle(id: Long): Set<Long> = if (id in this) this - id else this + id
