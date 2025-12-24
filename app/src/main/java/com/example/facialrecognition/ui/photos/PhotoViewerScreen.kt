package com.example.facialrecognition.ui.photos

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage

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

    val localPagerState = rememberPagerState(
        initialPage = uiState.initialIndex,
        pageCount = { uiState.photos.size }
    )
    
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
    ) {
        // Simple swipeable photo pager - no zoom
        HorizontalPager(
            state = localPagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            AsyncImage(
                model = uiState.photos[page].uri,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Top Bar
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${localPagerState.currentPage + 1} / ${uiState.photos.size}",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
                if (uiState.photos.isNotEmpty()) {
                    Text(
                        text = com.example.facialrecognition.util.DateUtils.formatDate(uiState.photos[localPagerState.currentPage].dateAdded),
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
