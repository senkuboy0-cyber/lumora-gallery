package app.lumora.gallery.domain.usecase

class GenerateShareCodeUseCase {
    operator fun invoke(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..6).map { chars.random() }.joinToString("")
    }
}
