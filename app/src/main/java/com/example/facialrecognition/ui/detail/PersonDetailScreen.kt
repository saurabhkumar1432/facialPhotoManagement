package com.example.facialrecognition.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.facialrecognition.data.local.entity.Photo
import com.example.facialrecognition.ui.theme.PrimaryBlue
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import com.example.facialrecognition.R
import com.example.facialrecognition.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailScreen(
    personId: Long,
    onBackClick: () -> Unit,
    onPhotoClick: (Long) -> Unit,
    onPlayClick: () -> Unit,
    viewModel: PersonDetailViewModel = viewModel(
        factory = PersonDetailViewModel.Factory(
            LocalContext.current.applicationContext as android.app.Application,
            personId
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showRenameDialog by remember { androidx.compose.runtime.mutableStateOf(false) }
    var showMenu by remember { androidx.compose.runtime.mutableStateOf(false) }

    if (showRenameDialog) {
        RenamePersonDialog(
            currentName = uiState.person?.name ?: "",
            onConfirm = { newName ->
                viewModel.renamePerson(newName)
            },
            onDismiss = { showRenameDialog = false }
        )
    }

    // Group photos by date
    val groupedPhotos = remember(uiState.photos) {
        uiState.photos.groupBy { photo ->
            com.example.facialrecognition.util.DateUtils.formatMonthYear(photo.dateAdded * 1000L)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.PaddingExtraLarge, vertical = Dimens.PaddingLarge),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(Dimens.AvatarSizeSmall)
                    .clip(CircleShape)
                    .background(Color.Transparent)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = stringResource(R.string.back_desc),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            
            // Title (Opacity transition could be added with scroll state)
            Text(
                text = uiState.person?.name ?: "",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.alpha(if (uiState.isLoading) 0f else 1f) // Simple visibility toggle
            )

            // Spacer to balance layout
            Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.more_options_desc),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.delete_person)) },
                        onClick = {
                            showMenu = false
                            viewModel.deletePerson {
                                onBackClick()
                            }
                        }
                    )
                }
            }
        }

        if (uiState.isLoading) {
             Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                 CircularProgressIndicator(color = PrimaryBlue)
             }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                contentPadding = PaddingValues(bottom = Dimens.BottomNavHeight),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(horizontal = 2.dp)
            ) {
                // Profile Header Section (as first item spanning full width)
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp, top = 8.dp)
                    ) {
                        // Avatar
                        Box(modifier = Modifier.padding(bottom = Dimens.PaddingExtraLarge)) {
                            val avatarUri = uiState.person?.avatarUri
                            if (avatarUri != null) {
                                Box(
                                    modifier = Modifier
                                        .size(Dimens.AvatarSizeProfile)
                                        .clip(CircleShape)
                                        .border(Dimens.BorderWidthThick, MaterialTheme.colorScheme.surface, CircleShape)
                                ) {
                                    AsyncImage(
                                        model = avatarUri,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(Dimens.AvatarSizeProfile)
                                        .clip(CircleShape)
                                        .border(Dimens.BorderWidthThick, MaterialTheme.colorScheme.surface, CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = uiState.person?.name?.take(2)?.uppercase() ?: "",
                                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Info
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(bottom = Dimens.PaddingSmall).clickable { showRenameDialog = true }
                        ) {
                            Text(
                                text = uiState.person?.name ?: stringResource(R.string.unknown_person),
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(Dimens.PaddingMedium))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.edit_name_desc),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = stringResource(R.string.photos_count_format, uiState.photos.size),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = Dimens.PaddingDoubleLarge)
                        )

                        // Actions
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { 
                                    // Select all photos
                                    uiState.photos.forEach { photo ->
                                        viewModel.toggleSelection(photo.id)
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(Dimens.CornerRadiusRound)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(Dimens.PaddingMedium))
                                Text(if (uiState.selectedPhotoIds.isEmpty()) stringResource(R.string.select_all) else stringResource(R.string.clear_selection))
                            }
                            Button(
                                onClick = { 
                                    if (uiState.selectedPhotoIds.isNotEmpty()) {
                                        shareImages(context, uiState.photos, uiState.selectedPhotoIds)
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryBlue,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(Dimens.CornerRadiusRound),
                                enabled = uiState.selectedPhotoIds.isNotEmpty()
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(Dimens.PaddingMedium))
                                Text(stringResource(R.string.share))
                            }
                            Button(
                                onClick = { 
                                    if (uiState.photos.isNotEmpty()) {
                                        onPlayClick()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(Dimens.CornerRadiusRound)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(Dimens.PaddingMedium))
                                Text(stringResource(R.string.play_slideshow))
                            }
                        }
                    }
                }

                groupedPhotos.forEach { (date, photosInGroup) ->
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = stringResource(R.string.items_count_format, photosInGroup.size),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    }
                    items(photosInGroup) { photo ->
                        PhotoItem(
                            photo = photo,
                            isSelected = uiState.selectedPhotoIds.contains(photo.id),
                            onClick = { 
                                if (uiState.selectedPhotoIds.isNotEmpty()) {
                                    viewModel.toggleSelection(photo.id) 
                                } else {
                                    onPhotoClick(photo.id)
                                }
                            },
                            onLongClick = { viewModel.toggleSelection(photo.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoItem(photo: Photo, isSelected: Boolean, onClick: () -> Unit, onLongClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        AsyncImage(
            model = photo.uri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PrimaryBlue.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = stringResource(R.string.selected_desc),
                    tint = Color.White,
                    modifier = Modifier.size(Dimens.IconSizeLarge)
                )
            }
        }
    }
}

fun shareImages(context: android.content.Context, allPhotos: List<Photo>, selectedIds: Set<Long>) {
    val selectedPhotos = allPhotos.filter { selectedIds.contains(it.id) }
    val uris = ArrayList<Uri>()
    selectedPhotos.forEach { photo ->
        uris.add(Uri.parse(photo.uri))
    }
    
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND_MULTIPLE
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        type = "image/*"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_intent_title)))
}



@Composable
fun RenamePersonDialog(
    currentName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { androidx.compose.runtime.mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.rename_person)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.dialog_name_label)) },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(name)
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.dialog_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}
