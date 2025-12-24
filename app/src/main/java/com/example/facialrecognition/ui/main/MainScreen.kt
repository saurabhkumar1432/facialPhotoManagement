package com.example.facialrecognition.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import androidx.compose.ui.res.stringResource
import com.example.facialrecognition.R
import com.example.facialrecognition.data.local.entity.Person
import com.example.facialrecognition.data.local.entity.Photo
import com.example.facialrecognition.ui.theme.Dimens
import com.example.facialrecognition.ui.theme.PrimaryBlue

@Composable
fun MainScreen(
    onPersonClick: (Long) -> Unit,
    onAllPhotosClick: () -> Unit,
    onPhotoClick: (Long) -> Unit,
    onSearchClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingLarge)
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_launcher_new),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(Dimens.AvatarSizeSmall)
                        .clip(CircleShape)
                )
                Text(
                    text = stringResource(R.string.app_title),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)) {
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .size(Dimens.AvatarSizeSmall)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                ) {
                    Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search_desc), tint = MaterialTheme.colorScheme.onBackground)
                }
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .size(Dimens.AvatarSizeSmall)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings_desc), tint = MaterialTheme.colorScheme.onBackground)
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = Dimens.BottomNavHeight), // Space for bottom nav
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingLarge),
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingLarge),
            modifier = Modifier.padding(horizontal = Dimens.PaddingExtraLarge)
        ) {
            // Privacy Indicator
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier.padding(vertical = Dimens.PaddingLarge)
                ) {
                    Surface(
                        color = PrimaryBlue.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(Dimens.CornerRadiusRound),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = Dimens.PaddingLarge, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null, // Decorative
                                tint = PrimaryBlue,
                                modifier = Modifier.size(Dimens.IconSizeSmall)
                            )
                            Text(
                                text = stringResource(R.string.privacy_indicator),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                color = PrimaryBlue
                            )
                        }
                    }
                }
            }

            // Stats Section
            item(span = { GridItemSpan(2) }) {
                Column {
                    Text(
                        text = stringResource(R.string.library_stats),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = Dimens.PaddingLarge)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingLarge)
                    ) {
                        StatItem(
                            icon = Icons.Default.Image,
                            value = "${uiState.photoCount}",
                            label = stringResource(R.string.stat_photos),
                            color = Color(0xFF3B82F6), // Blue-500
                            onClick = onAllPhotosClick,
                            modifier = Modifier.weight(1f)
                        )
                        StatItem(
                            icon = Icons.Default.Group,
                            value = "${uiState.peopleCount}",
                            label = stringResource(R.string.stat_people),
                            color = Color(0xFFA855F7), // Purple-500
                            onClick = onSearchClick,
                            modifier = Modifier.weight(1f)
                        )
                        StatItem(
                            icon = Icons.Default.Face,
                            value = "${uiState.faceCount}",
                            label = stringResource(R.string.stat_faces),
                            color = Color(0xFF10B981), // Emerald-500
                            onClick = onSearchClick,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // People Carousel
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.padding(top = Dimens.PaddingDoubleLarge)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = Dimens.PaddingLarge),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.section_people_pets),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraLarge),
                        contentPadding = PaddingValues(bottom = Dimens.PaddingExtraLarge)
                    ) {
                        items(uiState.people) { person ->
                            PersonAvatarItem(person = person, onClick = { onPersonClick(person.id) })
                        }
                    }
                }
            }

            // Recent Highlights Header
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.PaddingExtraLarge, bottom = Dimens.PaddingLarge),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.section_just_added),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(onClick = { viewModel.toggleSortOrder() }) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = stringResource(R.string.sort_desc), // "Sort"
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }


            // Recent Photos Grid
            if (uiState.recentPhotos.isNotEmpty()) {
                // Large Item (First photo)
                item(span = { GridItemSpan(2) }) {
                    val photo = uiState.recentPhotos.first()
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.CardElevation),
                        modifier = Modifier
                            .aspectRatio(16f / 9f)
                            .fillMaxWidth()
                            .clickable { onPhotoClick(photo.id) }
                    ) {
                        Box {
                            AsyncImage(
                                model = photo.uri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                                            startY = 300f
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(Dimens.PaddingExtraLarge)
                            ) {
                                Text(
                                    text = stringResource(R.string.recent_photo_label),
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.White
                                )
                                Text(
                                    text = if (uiState.recentPhotos.isNotEmpty()) {
                                        com.example.facialrecognition.util.DateUtils.formatRelativeTime(uiState.recentPhotos.first().dateAdded * 1000L)
                                    } else {
                                        stringResource(R.string.just_now)
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }

                // Other photos
                items(uiState.recentPhotos.drop(1).take(4)) { photo ->
                    Card(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable { onPhotoClick(photo.id) },
                        shape = RoundedCornerShape(Dimens.CornerRadiusMedium),
                        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.CardElevation)
                    ) {
                        AsyncImage(
                            model = photo.uri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            item(span = { GridItemSpan(2) }) {
                Button(
                    onClick = onAllPhotosClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.PaddingDoubleLarge),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(Dimens.BorderWidth, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Text(stringResource(R.string.view_all_photos))
                }
            }
        }
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(Dimens.PaddingLarge),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.IconSizeLarge)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp) // Keep tiny icon size for now or param
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PersonAvatarItem(person: Person, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium),
        modifier = Modifier
            .width(Dimens.AvatarSizeLarge)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.AvatarSizeLarge)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(PrimaryBlue, Color(0xFFA855F7)) // Blue to Purple
                    )
                )
                .padding(2.dp) // Border width
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            // Avatar Image (Placeholder for now if no image url in Person entity, assuming Person has uri or similar)
            // Since Person entity might not have a cover photo URI readily available in this context without joining, 
            // I'll use a placeholder or initials.
            // Ideally, the ViewModel should provide a PersonUiModel with a cover photo.
            
            if (person.avatarUri != null) {
                AsyncImage(
                    model = person.avatarUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = person.name.take(2).uppercase(),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Text(
            text = person.name,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            maxLines = 1,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
