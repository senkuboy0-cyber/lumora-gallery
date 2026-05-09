package app.lumora.gallery.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Background = Color(0xFF0A0A0F)
val Surface = Color(0xFF13131A)
val SurfaceVar = Color(0xFF1E1E2A)
val Primary = Color(0xFFA78BFA)
val Secondary = Color(0xFF60A5FA)
val Accent = Color(0xFFF472B6)
val GlassBg = Color(0x40FFFFFF)
val GlassBorder = Color(0x30FFFFFF)

private val LumoraDarkScheme = darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Accent,
    background = Background,
    surface = Surface,
    surfaceVariant = SurfaceVar,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun LumoraTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LumoraDarkScheme, content = content)
}
