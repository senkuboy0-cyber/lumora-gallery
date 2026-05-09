package app.lumora.gallery.domain.model

data class Album(
    val name: String,
    val count: Int,
    val coverItems: List<MediaItem> = emptyList()
)
