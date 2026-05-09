package app.lumora.gallery.service.ai

import kotlinx.coroutines.delay

class BackgroundRemovalService {
    suspend fun removeBackground(modelDownloaded: Boolean): Result<Unit> {
        if (!modelDownloaded) return Result.failure(IllegalStateException("AI model is not downloaded"))
        delay(200)
        return Result.success(Unit)
    }
}
