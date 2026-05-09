package app.lumora.gallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import app.lumora.gallery.presentation.LumoraApp
import app.lumora.gallery.presentation.LumoraViewModel
import app.lumora.gallery.presentation.theme.LumoraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            LumoraTheme {
                val viewModel: LumoraViewModel = viewModel()
                LumoraApp(viewModel = viewModel)
            }
        }
    }
}
