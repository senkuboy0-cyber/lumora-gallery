package app.lumora.gallery.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lumora.gallery.domain.model.Album
import app.lumora.gallery.domain.model.MediaItem
import app.lumora.gallery.presentation.components.GlassCard
import app.lumora.gallery.presentation.theme.Background
import app.lumora.gallery.presentation.theme.Primary
import app.lumora.gallery.presentation.theme.Secondary
import coil3.compose.AsyncImage

@Composable
fun LumoraApp(viewModel: LumoraViewModel) {
    val state by viewModel.uiState.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            viewModel.clearMessage()
        }
    }
    Scaffold(snackbarHost = { SnackbarHost(snackbar) }, containerColor = Background) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).background(Background)) {
            if (!state.settings.firstLaunchComplete) {
                PermissionOnboarding(
                    onGrant = { viewModel.completeOnboarding() },
                    onDownload = { viewModel.downloadAiModel() },
                    onSkip = { viewModel.skipAiModel() }
                )
            } else {
                when (state.currentScreen) {
                    LumoraScreen.Home -> HomeScreen(state, viewModel)
                    LumoraScreen.Search -> SearchScreen(state, viewModel)
                    LumoraScreen.Viewer -> ViewerScreen(state, viewModel)
                    LumoraScreen.Editor -> EditorScreen(state, viewModel)
                    LumoraScreen.Share -> ShareScreen(state, viewModel)
                    LumoraScreen.Locker -> LockerScreen(viewModel)
                    LumoraScreen.RecycleBin -> RecycleBinScreen(state, viewModel)
                    LumoraScreen.Settings -> SettingsScreen(state, viewModel)
                    LumoraScreen.StorageInfo -> StorageInfoScreen(state, viewModel)
                }
            }
        }
    }
}

@Composable
private fun PermissionOnboarding(onGrant: () -> Unit, onDownload: () -> Unit, onSkip: () -> Unit) {
    var showAiPrompt by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        LogoMark(88)
        Spacer(Modifier.height(20.dp))
        Text("Welcome to Lumora", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))
        PermissionCard(Icons.Outlined.Photo, "Media Access", "To display your photos and videos")
        PermissionCard(Icons.Outlined.Info, "Location", "Required for Nearby Share")
        PermissionCard(Icons.Outlined.Share, "WiFi", "For fast file transfers")
        Spacer(Modifier.height(24.dp))
        GradientButton("Grant Permissions") {
            onGrant()
            showAiPrompt = true
        }
    }
    if (showAiPrompt) AiPrompt(onDownload = onDownload, onSkip = onSkip)
}

@Composable
private fun HomeScreen(state: LumoraUiState, viewModel: LumoraViewModel) {
    var moreOpen by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            TopBar(title = "Lumora", onSearch = { viewModel.open(LumoraScreen.Search) })
            Spacer(Modifier.height(16.dp))
            HeroCard(state.media.firstOrNull(), state.media.size) { viewModel.selectTab(HomeTab.Photos) }
            Spacer(Modifier.height(20.dp))
            SectionTitle("Quick Access Albums")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.albums.ifEmpty { quickAlbums }) { album -> AlbumChip(album) }
            }
            Spacer(Modifier.height(20.dp))
            AnimatedVisibility(visible = state.media.size > 5) { MemoryCard() }
            Spacer(Modifier.height(12.dp))
            if (state.selectedTab == HomeTab.Photos) PhotosGrid(state, viewModel) else AlbumsGrid(state, viewModel)
        }
        BottomPill(state, viewModel, onMore = { moreOpen = true }, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp))
        if (moreOpen) MoreSheet(onDismiss = { moreOpen = false }, viewModel = viewModel)
    }
}

@Composable
private fun PhotosGrid(state: LumoraUiState, viewModel: LumoraViewModel, showDeleted: Boolean = false) {
    val items = if (showDeleted) state.media.filter { it.isDeleted } else state.media.filterNot { it.isDeleted }
    LazyVerticalGrid(columns = GridCells.Fixed(state.settings.gridColumns), contentPadding = PaddingValues(bottom = 96.dp), horizontalArrangement = Arrangement.spacedBy(2.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        items(items) { item ->
            MediaTile(
                item = item,
                selected = state.selectedIds.contains(item.id),
                onClick = { viewModel.openViewer(item) },
                onLongClick = { viewModel.toggleSelect(item.id) }
            )
        }
    }
}

@Composable
private fun AlbumsGrid(state: LumoraUiState, viewModel: LumoraViewModel) {
    Box(Modifier.fillMaxSize()) {
        LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(bottom = 96.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(state.albums) { album -> AlbumCard(album) }
        }
        FloatingActionButton(onClick = { }, modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp), containerColor = Primary) {
            Icon(Icons.Outlined.Add, contentDescription = "New album")
        }
    }
}

@Composable
private fun ViewerScreen(state: LumoraUiState, viewModel: LumoraViewModel) {
    val item = state.selectedMedia ?: return HomeScreen(state, viewModel)
    var chromeVisible by remember { mutableStateOf(true) }
    Box(Modifier.fillMaxSize().background(Color.Black).clickable { chromeVisible = !chromeVisible }) {
        AsyncImage(model = item.uri, contentDescription = item.fileName, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
        AnimatedVisibility(chromeVisible, Modifier.align(Alignment.TopCenter)) {
            TopBar(title = item.fileName, onBack = { viewModel.backHome() })
        }
        AnimatedVisibility(chromeVisible, Modifier.align(Alignment.BottomCenter)) {
            GlassCard(Modifier.fillMaxWidth().padding(16.dp)) {
                Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceAround) {
                    IconButton(onClick = { viewModel.toggleSelect(item.id); viewModel.deleteSelected() }) { Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = Color.White) }
                    IconButton(onClick = { viewModel.open(LumoraScreen.Editor) }) { Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = Color.White) }
                    IconButton(onClick = { viewModel.open(LumoraScreen.Share) }) { Icon(Icons.Outlined.Share, contentDescription = "Share", tint = Color.White) }
                    IconButton(onClick = { viewModel.toggleFavourite(item.id) }) { Icon(Icons.Outlined.StarOutline, contentDescription = "Favourite", tint = Color.White) }
                    IconButton(onClick = { }) { Icon(Icons.Outlined.Info, contentDescription = "Info", tint = Color.White) }
                }
            }
        }
    }
}

@Composable
private fun EditorScreen(state: LumoraUiState, viewModel: LumoraViewModel) {
    var saveDialog by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        TopBar(title = "Editor", onBack = { viewModel.backHome() })
        Box(Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(20.dp)).background(Color.Black), contentAlignment = Alignment.Center) {
            state.selectedMedia?.let { AsyncImage(model = it.uri, contentDescription = it.fileName, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit) }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 16.dp)) {
            items(listOf("Auto", "Crop", "Filter", "Adjust", "AI Edit", "BG Remove", "Blur")) { tool -> GlassTool(tool) }
        }
        GradientButton("Done") { saveDialog = true }
    }
    if (saveDialog) {
        AlertDialog(
            onDismissRequest = { saveDialog = false },
            title = { Text("Save changes") },
            text = { Text("Choose how Lumora should save this edit.") },
            confirmButton = { TextButton(onClick = { saveDialog = false; viewModel.backHome() }) { Text("Save as new copy") } },
            dismissButton = { TextButton(onClick = { saveDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun ShareScreen(state: LumoraUiState, viewModel: LumoraViewModel) {
    LaunchedEffect(Unit) { if (state.shareCode.isBlank()) viewModel.generateRemoteCode() }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        TopBar(title = "Share", onBack = { viewModel.backHome() })
        PermissionCard(Icons.Outlined.Share, "Nearby Share", "Scan for nearby Lumora devices and transfer with WiFi Direct")
        PermissionCard(Icons.Outlined.Info, "Remote Share", "Use a 6 character code that expires in 5 minutes")
        Spacer(Modifier.height(24.dp))
        Text(state.shareCode.ifBlank { "------" }, color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Bold)
        Text("Code expires in 5 minutes", color = Color.LightGray)
        Spacer(Modifier.height(16.dp))
        LinearProgressIndicator(progress = { 0.35f }, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun LockerScreen(viewModel: LumoraViewModel) {
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Outlined.Lock, contentDescription = "Locker", tint = Color.White, modifier = Modifier.size(72.dp))
        Text("Enter PIN", color = Color.White, fontSize = 24.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(24.dp)) {
            repeat(4) { Box(Modifier.size(14.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.35f))) }
        }
        Text("Private files are encrypted in internal storage", color = Color.LightGray)
        Spacer(Modifier.height(24.dp))
        GradientButton("Confirm") { viewModel.backHome() }
    }
}

@Composable
private fun RecycleBinScreen(state: LumoraUiState, viewModel: LumoraViewModel) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        TopBar(title = "Recycle Bin", onBack = { viewModel.backHome() })
        PhotosGrid(state, viewModel, showDeleted = true)
    }
}

@Composable
private fun SettingsScreen(state: LumoraUiState, viewModel: LumoraViewModel) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { TopBar(title = "Settings", onBack = { viewModel.backHome() }) }
        items(listOf("Appearance", "AI Features", "Security", "Transfer", "About")) { title -> SettingsRow(title) }
        item { SettingsRow("Grid columns: ${state.settings.gridColumns}") }
    }
}

@Composable
private fun SearchScreen(state: LumoraUiState, viewModel: LumoraViewModel) {
    var query by remember { mutableStateOf("") }
    val results = state.media.filter { it.fileName.contains(query, ignoreCase = true) || it.albumName.contains(query, ignoreCase = true) }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        TopBar(title = "Search", onBack = { viewModel.backHome() })
        OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Search photos, dates, and albums") })
        PhotosGrid(state.copy(media = results), viewModel)
    }
}

@Composable
private fun StorageInfoScreen(state: LumoraUiState, viewModel: LumoraViewModel) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        TopBar(title = "Storage Info", onBack = { viewModel.backHome() })
        SettingsRow("Photos and videos: ${state.media.size}")
        SettingsRow("Locker usage: encrypted internal storage")
        SettingsRow("Largest files: ${state.media.maxByOrNull { it.sizeBytes }?.fileName.orEmpty()}")
    }
}

@Composable
private fun TopBar(title: String, onBack: (() -> Unit)? = null, onSearch: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        if (onBack != null) IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = Color.White) } else LogoMark(34)
        Spacer(Modifier.width(10.dp))
        Text(title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (onSearch != null) IconButton(onClick = onSearch) { Icon(Icons.Outlined.Search, contentDescription = "Search", tint = Color.White) }
    }
}

@Composable
private fun HeroCard(item: MediaItem?, count: Int, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f).clip(RoundedCornerShape(24.dp)).clickable(onClick = onClick)) {
        if (item != null) AsyncImage(model = item.uri, contentDescription = "Recently Saved", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.72f)))))
        Column(Modifier.align(Alignment.BottomStart).padding(20.dp)) {
            Text("Recently Saved", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text("$count items", color = Color.LightGray)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MediaTile(item: MediaItem, selected: Boolean, onClick: () -> Unit, onLongClick: () -> Unit) {
    Box(Modifier.aspectRatio(1f).clip(RoundedCornerShape(4.dp)).background(Color.DarkGray).combinedClickable(onClick = onClick, onLongClick = onLongClick)) {
        AsyncImage(model = item.uri, contentDescription = item.fileName, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        if (item.isVideo) GlassCard(Modifier.align(Alignment.BottomStart).padding(6.dp)) { Text("01:12", color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 11.sp) }
        if (item.isFavourite) Icon(Icons.Outlined.StarOutline, contentDescription = "Favourite", tint = Color.White, modifier = Modifier.align(Alignment.TopEnd).padding(6.dp))
        if (selected) Box(Modifier.fillMaxSize().background(Primary.copy(alpha = 0.35f)))
    }
}

@Composable
private fun AlbumChip(album: Album) {
    GlassCard(Modifier.size(120.dp)) {
        Box(Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.BottomStart) {
            Text(album.name, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AlbumCard(album: Album) {
    GlassCard(Modifier.fillMaxWidth().height(178.dp)) {
        Column(Modifier.padding(14.dp)) {
            Icon(Icons.Outlined.Folder, contentDescription = album.name, tint = Color.White, modifier = Modifier.size(44.dp))
            Spacer(Modifier.weight(1f))
            Text(album.name, color = Color.White, fontWeight = FontWeight.Bold)
            Text("${album.count} items", color = Color.LightGray)
        }
    }
}

@Composable
private fun BottomPill(state: LumoraUiState, viewModel: LumoraViewModel, onMore: () -> Unit, modifier: Modifier) {
    GlassCard(modifier) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.open(LumoraScreen.Share) }) { Icon(Icons.Outlined.Share, contentDescription = "Share", tint = Color.White) }
            TextButton(onClick = { viewModel.selectTab(HomeTab.Photos) }) { Text("Photos", color = if (state.selectedTab == HomeTab.Photos) Primary else Color.White) }
            TextButton(onClick = { viewModel.selectTab(HomeTab.Albums) }) { Text("Albums", color = if (state.selectedTab == HomeTab.Albums) Primary else Color.White) }
            IconButton(onClick = onMore) { Icon(Icons.Outlined.MoreVert, contentDescription = "More", tint = Color.White) }
        }
    }
}

@Composable
private fun MoreSheet(onDismiss: () -> Unit, viewModel: LumoraViewModel) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Background) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SheetAction("Locker", Icons.Outlined.Lock) { viewModel.open(LumoraScreen.Locker); onDismiss() }
            SheetAction("Recycle Bin", Icons.Outlined.Delete) { viewModel.open(LumoraScreen.RecycleBin); onDismiss() }
            SheetAction("Favourites", Icons.Outlined.StarOutline) { onDismiss() }
            SheetAction("Storage Info", Icons.Outlined.Info) { viewModel.open(LumoraScreen.StorageInfo); onDismiss() }
            SheetAction("Settings", Icons.Outlined.Settings) { viewModel.open(LumoraScreen.Settings); onDismiss() }
        }
    }
}

@Composable
private fun SheetAction(label: String, icon: ImageVector, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable(onClick = onClick).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = label, tint = Color.White)
        Spacer(Modifier.width(16.dp))
        Text(label, color = Color.White)
    }
}

@Composable
private fun PermissionCard(icon: ImageVector, title: String, body: String) {
    GlassCard(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = title, tint = Color.White)
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(body, color = Color.LightGray)
            }
        }
    }
}

@Composable
private fun AiPrompt(onDownload: () -> Unit, onSkip: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onSkip, containerColor = Background) {
        Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("AI Photo Features", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Text("Background Removal and AI editing require a small AI model to be downloaded once.", color = Color.LightGray)
            Spacer(Modifier.height(18.dp))
            Row {
                Button(onClick = onDownload) { Text("Download Now") }
                Spacer(Modifier.width(12.dp))
                TextButton(onClick = onSkip) { Text("Not Now") }
            }
        }
    }
}

@Composable
private fun GradientButton(text: String, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(54.dp).clip(RoundedCornerShape(28.dp)).background(Brush.horizontalGradient(listOf(Primary, Secondary))).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(text, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun LogoMark(size: Int) {
    Box(Modifier.size(size.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Primary, Secondary))), contentAlignment = Alignment.Center) {
        Text("L", color = Color.White, fontSize = (size / 2).sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SectionTitle(text: String) { Text(text, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp)) }
@Composable
private fun MemoryCard() { GlassCard(Modifier.fillMaxWidth()) { Text("On this day 1 year ago", color = Color.White, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold) } }
@Composable
private fun GlassTool(text: String) { GlassCard { Text(text, color = Color.White, modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) } }
@Composable
private fun SettingsRow(text: String) { GlassCard(Modifier.fillMaxWidth()) { Text(text, color = Color.White, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.SemiBold) } }

private val quickAlbums = listOf(
    Album("Camera Roll", 0),
    Album("Screenshots", 0),
    Album("Videos", 0),
    Album("Favourites", 0),
    Album("Downloads", 0)
)
