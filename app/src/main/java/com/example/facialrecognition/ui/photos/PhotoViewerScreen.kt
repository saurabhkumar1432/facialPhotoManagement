package com.example.facialrecognition.ui.photos

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.facialrecognition.data.local.entity.Photo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoViewerScreen(
    source: String,
    sourceId: Long,
    startPhotoId: Long,
    autoPlay: Boolean = false,
    onBackClick: () -> Unit
) {
    val application = LocalContext.current.applicationContext as android.app.Application
    val viewModel: PhotoViewerViewModel = viewModel(
        factory = PhotoViewerViewModel.Factory(application, source, sourceId, startPhotoId)
    )
    val uiState by viewModel.uiState.collectAsState()
    var pagerState: androidx.compose.foundation.pager.PagerState? by remember { mutableStateOf(null) }
    
    // Initialize PagerState once data is loaded
    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading && uiState.photos.isNotEmpty()) {
            // We can't easily recreate PagerState with a new initialPage if it's already remembered,
            // but since uiState.isLoading starts as true, this block runs once when data arrives.
            // However, rememberPagerState is a Composable function.
            // Better approach: Derived key. 
        }
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    if (uiState.photos.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            Text("No photos found", color = Color.White)
        }
        return
    }

    // Key the pager state to the photos list size or id to ensure it resets if data changes drastically (unlikely here)
    // We pass initialPage to the remember call.
    val localPagerState = rememberPagerState(
        initialPage = uiState.initialIndex,
        pageCount = { uiState.photos.size }
    )
    
    var showControls by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(autoPlay) }

    // Auto-play logic
    LaunchedEffect(isPlaying) {
        if (isPlaying && uiState.photos.isNotEmpty()) {
            while (true) {
                delay(3000)
                val nextPage = (localPagerState.currentPage + 1) % uiState.photos.size
                localPagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { showControls = !showControls }
    ) {
        HorizontalPager(
            state = localPagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            ZoomableImage(
                imageUrl = uiState.photos[page].uri,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Top Bar
        if (showControls) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .statusBarsPadding()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Use localPagerState.currentPage which updates as user swipes
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${localPagerState.currentPage + 1} / ${uiState.photos.size}",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (uiState.photos.isNotEmpty()) {
                        Text(
                            text = com.example.facialrecognition.util.DateUtils.formatDate(uiState.photos[localPagerState.currentPage].dateAdded * 1000L),
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                // Slideshow Toggle
                IconButton(onClick = { isPlaying = !isPlaying }) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ZoomableImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 3f)
        val extraWidth = (scale - 1) * 1000 // Approximate width constraint
        val extraHeight = (scale - 1) * 1000 
        
        // Simple bounds check (can be improved)
        if (scale > 1f) {
             offset += offsetChange
        } else {
             offset = Offset.Zero
        }
    }

    Box(
        modifier = modifier
            .clipToBounds() // Ensure content doesn't overflow when zoomed
            .transformable(state = state)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
        )
    }
}


