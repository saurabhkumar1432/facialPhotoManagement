package com.example.facialrecognition.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.facialrecognition.data.local.entity.Photo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailScreen(
    personId: Long,
    onBackClick: () -> Unit,
    viewModel: PersonDetailViewModel = viewModel(
        factory = PersonDetailViewModel.Factory(
            LocalContext.current.applicationContext as android.app.Application,
            personId
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.person?.name ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.selectedPhotoIds.isNotEmpty()) {
                        IconButton(onClick = {
                            shareImages(context, uiState.photos, uiState.selectedPhotoIds)
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
             Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                 CircularProgressIndicator()
             }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                contentPadding = PaddingValues(4.dp),
                modifier = Modifier.padding(paddingValues)
            ) {
                items(uiState.photos) { photo ->
                   PhotoItem(
                       photo = photo,
                       isSelected = uiState.selectedPhotoIds.contains(photo.id),
                       onClick = { viewModel.toggleSelection(photo.id) }
                   )
                }
            }
        }
    }
}

@Composable
fun PhotoItem(photo: Photo, isSelected: Boolean, onClick: () -> Unit) {
    Box(modifier = Modifier.padding(2.dp).aspectRatio(1f).clickable { onClick() }) {
        AsyncImage(
            model = photo.uri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = Color.White
                )
            }
        }
    }
}

fun shareImages(context: android.content.Context, allPhotos: List<Photo>, selectedIds: Set<Long>) {
    val selectedPhotos = allPhotos.filter { selectedIds.contains(it.id) }
    val uris = ArrayList<Uri>()
    selectedPhotos.forEach { photo ->
        uris.add(Uri.parse(photo.uri)) // Note: might need FileProvider if targeting API 24+ and sharing internal files, but these possess Uri from MediaStore which are shareable if we have read permission? 
        // MediaStore URIs (content://) are shareable usually.
    }
    
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND_MULTIPLE
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        type = "image/*"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share images to.."))
}
