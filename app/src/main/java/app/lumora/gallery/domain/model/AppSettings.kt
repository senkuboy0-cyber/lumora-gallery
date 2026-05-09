package app.lumora.gallery.domain.model

enum class ThemeMode { Dark, Light, System }
enum class AiProvider { Gemini, OpenRouter, CustomUrl }
enum class TransferMode { Nearby, Remote }
enum class TransferQuality { Original, Compressed }

data class AppSettings(
    val firstLaunchComplete: Boolean = false,
    val aiModelDownloaded: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.Dark,
    val gridColumns: Int = 3,
    val aiProvider: AiProvider = AiProvider.Gemini,
    val customAiUrl: String = "",
    val transferMode: TransferMode = TransferMode.Nearby,
    val transferQuality: TransferQuality = TransferQuality.Original,
    val autoLockSeconds: Int = 5,
    val failedAttemptLimit: Int = 5
)
