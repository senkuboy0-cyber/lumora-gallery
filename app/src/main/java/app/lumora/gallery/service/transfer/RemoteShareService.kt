package app.lumora.gallery.service.transfer

import app.lumora.gallery.domain.usecase.GenerateShareCodeUseCase

class RemoteShareService(private val generateShareCode: GenerateShareCodeUseCase = GenerateShareCodeUseCase()) {
    fun createSession(): RemoteShareSession {
        val expiresAt = System.currentTimeMillis() + 5 * 60 * 1000
        return RemoteShareSession(code = generateShareCode(), expiresAtMillis = expiresAt, stunServer = "stun:stun.l.google.com:19302")
    }
}

data class RemoteShareSession(val code: String, val expiresAtMillis: Long, val stunServer: String)
