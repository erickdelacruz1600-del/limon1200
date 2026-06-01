package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Movie
import com.example.model.SubtitleTrack
import com.example.ui.MovieViewModel
import com.example.ui.Screen
import com.example.ui.components.tvFocusable

@Composable
fun DetailsScreen(
    movie: Movie,
    viewModel: MovieViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Default to the first subtitle track (e.g. Spanish) if available
    var selectedSubtitle by remember { mutableStateOf<SubtitleTrack?>(movie.subtitles.firstOrNull()) }
    val isFavorite by viewModel.favorites.collectAsState()
    val favorited = isFavorite.contains(movie.id)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1C1B1F)) // High Density Slate Black
    ) {
        // Landscape Backdrop filling the right half/background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .align(Alignment.TopEnd)
        ) {
            AsyncImage(
                model = movie.backdropUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Beautiful dark gradient mask fading to the solid black base
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0x661C1B1F), Color(0xFF1C1B1F)),
                            startY = 100f
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF1C1B1F), Color(0x771C1B1F), Color.Transparent),
                            endX = 1200f
                        )
                    )
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 80.dp, bottom = 40.dp)
        ) {
            // Main Content Layout
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(28.dp)
                ) {
                    // Vertical Film Poster
                    Card(
                        modifier = Modifier
                            .width(180.dp)
                            .height(260.dp)
                            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        AsyncImage(
                            model = movie.posterUrl,
                            contentDescription = "Póster",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Metadata details
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = movie.category.uppercase(),
                            color = Color(0xFFD0BCFF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = movie.title,
                            color = Color(0xFFE6E1E5),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 36.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Rating, Year, Duration row
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFD0BCFF),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${movie.rating}",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "${movie.year}",
                                color = Color(0xFFCAC4D0),
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = movie.duration,
                                color = Color(0xFFCAC4D0),
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF49454F), RoundedCornerShape(4.dp))
                                    .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "4K ULTRA HD",
                                    color = Color(0xFFD0BCFF),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Genre chips
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            movie.genres.forEach { genre ->
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF2B2930), RoundedCornerShape(16.dp))
                                        .border(1.dp, Color(0xFF49454F), RoundedCornerShape(16.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(text = genre, color = Color(0xFFE6E1E5), fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = movie.description,
                            color = Color(0xFFCAC4D0),
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            maxLines = 6,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Reproduce and Subtitle customization sections
            item {
                Spacer(modifier = Modifier.height(30.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // BIG PLAY KEY
                    Button(
                        onClick = { viewModel.navigateTo(Screen.Player(movie, selectedSubtitle)) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD0BCFF)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .tvFocusable(shape = RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color(0xFF381E72),
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "REPRODUCIR EN ALTA DEFINICIÓN",
                            color = Color(0xFF381E72),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    // ADD FAVORITES
                    Button(
                        onClick = { viewModel.toggleFavorite(movie.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF49454F)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .tvFocusable(shape = RoundedCornerShape(8.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = if (favorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (favorited) Color(0xFFF2B8B5) else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (favorited) "EN TU LISTA" else "AÑADIR A MI LISTA",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Selection Subtitle track for TCL TV
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "SELECCIONAR SUBTÍTULOS DISPONIBLES",
                    color = Color(0xFFD0BCFF),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Track Selector grid (horizontal focus-friendly rows)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Option 1: No subtitles (OFF)
                    val isNoneSelected = selectedSubtitle == null
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(55.dp)
                            .tvFocusable(shape = RoundedCornerShape(8.dp))
                            .background(
                                if (isNoneSelected) Color(0xFFD0BCFF).copy(alpha = 0.15f) else Color(0xFF2B2930),
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 1.5.dp,
                                color = if (isNoneSelected) Color(0xFFD0BCFF) else Color(0xFF49454F),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedSubtitle = null }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sin Subtítulos (OFF)",
                                color = if (isNoneSelected) Color(0xFFD0BCFF) else Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isNoneSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFFD0BCFF),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Options for subtitles
                    movie.subtitles.forEach { track ->
                        val isTrackSelected = selectedSubtitle?.code == track.code
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(55.dp)
                                .tvFocusable(shape = RoundedCornerShape(8.dp))
                                .background(
                                    if (isTrackSelected) Color(0xFFD0BCFF).copy(alpha = 0.15f) else Color(0xFF2B2930),
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = if (isTrackSelected) Color(0xFFD0BCFF) else Color(0xFF49454F),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedSubtitle = track }
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = track.language,
                                        color = if (isTrackSelected) Color(0xFFD0BCFF) else Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Subtítulo de red",
                                        color = Color(0xFFCAC4D0),
                                        fontSize = 10.sp
                                    )
                                }
                                if (isTrackSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFFD0BCFF),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Top Header Row with TV remote Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF2B2930), RoundedCornerShape(24.dp))
                    .border(2.dp, Color(0xFFD0BCFF).copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                    .tvFocusable(shape = RoundedCornerShape(24.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver al Catálogo",
                    tint = Color.White
                )
            }
        }
    }
}
