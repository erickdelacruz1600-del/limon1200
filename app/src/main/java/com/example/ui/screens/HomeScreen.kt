package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
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
import com.example.data.MovieRepository
import com.example.model.Movie
import com.example.ui.MovieViewModel
import com.example.ui.Screen
import com.example.ui.components.tvFocusable

@Composable
fun HomeScreen(
    viewModel: MovieViewModel,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    val movies by viewModel.moviesState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val featuredMovie = movies.firstOrNull() ?: MovieRepository.movies[0]
    val categories = movies.map { it.category }.distinct()
    
    val subtitleSize by viewModel.subtitleSize.collectAsState()
    var showSubtitleDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1C1B1F)) // High Density Slate Black
    ) {
        if (isLoading && movies.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = Color(0xFFD0BCFF),
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Cargando catálogo desde TMDB...",
                    color = Color(0xFFCAC4D0),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 40.dp)
            ) {
                // 1. Featured Screen Banner (Netflix TV Style)
                item {
                    FeaturedHeroBanner(
                        movie = featuredMovie,
                        onPlayClick = { viewModel.navigateTo(Screen.Player(featuredMovie, featuredMovie.subtitles.firstOrNull())) },
                        onDetailsClick = { onMovieClick(featuredMovie) }
                    )
                }

                // Nice network message overlay if TMDB fails or runs offline fallback
                if (errorMessage != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 12.dp)
                                .border(1.dp, Color(0xFFF2B8B5).copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2930)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color(0xFF8C1D18), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = errorMessage ?: "",
                                    color = Color(0xFFF2B8B5),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // 2. Movie Rows grouped by Category
                categories.forEach { category ->
                    item {
                        val catMovies = movies.filter { it.category == category }
                        com.example.ui.components.MovieRow(
                            title = category,
                            movies = catMovies,
                            onMovieClick = onMovieClick
                        )
                    }
                }
            }
        }

        // 3. Top Floating TV Navigation Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xEE1C1B1F), Color.Transparent)
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Logo for TV tcl in High Density style
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFD0BCFF), CircleShape)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "tv",
                        color = Color(0xFF381E72),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "TCL Cine+",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "MODO REMOTO ACTIVO",
                        color = Color(0xFFD0BCFF),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Subtitle Preferences Button with TV Focus state
            Row(
                modifier = Modifier
                    .tvFocusable(shape = RoundedCornerShape(20.dp))
                    .background(Color(0xFF2B2930), RoundedCornerShape(20.dp))
                    .border(2.dp, Color(0xFFD0BCFF).copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .clickable { showSubtitleDialog = true }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configurar Subtítulos",
                    tint = Color(0xFFD0BCFF),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Subtítulos: Talla ${subtitleSize.toInt()}px",
                    color = Color(0xFFE6E1E5),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 4. Quick Subtitle Customization Popup Overlay (Perfect for TV customization!)
        if (showSubtitleDialog) {
            AlertDialog(
                onDismissRequest = { showSubtitleDialog = false },
                containerColor = Color(0xFF2B2930),
                modifier = Modifier.border(2.dp, Color(0xFFD0BCFF), RoundedCornerShape(28.dp)),
                title = {
                    Text(
                        text = "Configuración de Subtítulos",
                        color = Color(0xFFE6E1E5),
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Ajusta el tamaño del texto para leer cómodamente en tu pantalla TCL desde el sofá:",
                            color = Color(0xFFCAC4D0),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Size selector grids (focus-friendly buttons)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(18f, 24f, 32f, 42f).forEach { size ->
                                val label = when(size) {
                                    18f -> "Pequ."
                                    24f -> "Med."
                                    32f -> "Gran."
                                    else -> "TCL XL"
                                }
                                val isSelected = subtitleSize == size
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(45.dp)
                                        .tvFocusable(shape = RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelected) Color(0xFFD0BCFF) else Color(0xFF49454F),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            2.dp,
                                            if (isSelected) Color.White else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { viewModel.updateSubtitleSize(size) }
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$label (${size.toInt()})",
                                        color = if (isSelected) Color(0xFF381E72) else Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Vista Previa de Subtítulos:",
                            color = Color(0xFFCAC4D0),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Demo Subtitle text
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .background(Color.Black, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "¡Bienvenidos a la Alta Definición!",
                                color = Color(viewModel.subtitleColor.collectAsState().value),
                                fontSize = (subtitleSize / 1.5f).sp, // Scaled for dialog
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                      }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showSubtitleDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD0BCFF)),
                            modifier = Modifier.tvFocusable(shape = RoundedCornerShape(8.dp))
                        ) {
                            Text("Aceptar", color = Color(0xFF381E72), fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
    }
}

@Composable
fun FeaturedHeroBanner(
    movie: Movie,
    onPlayClick: () -> Unit,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        // Landscape Background Image
        AsyncImage(
            model = movie.backdropUrl,
            contentDescription = "Fondo de ${movie.title}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark Radial/Linear cinematic overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x77000000),
                            Color(0xAA000000),
                            Color(0xFF1C1B1F)
                        ),
                        startY = 100f
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xEE1C1B1F),
                            Color(0x881C1B1F),
                            Color.Transparent
                        ),
                        endX = 1100f
                    )
                )
        )

        // Text & Action details
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(start = 24.dp, bottom = 24.dp, end = 24.dp, top = 60.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Tagline or category
            Text(
                text = "RECOMENDADA • ${movie.genres.joinToString(" / ").uppercase()}",
                color = Color(0xFFD0BCFF),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Title
            Text(
                text = movie.title,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Rating/Year/Duration row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFD0BCFF),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "${movie.rating}",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${movie.year}",
                    color = Color(0xFFCAC4D0),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = movie.duration,
                    color = Color(0xFFCAC4D0),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                // HD tag
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

            // Description
            Text(
                text = movie.description,
                color = Color(0xFFCAC4D0),
                fontSize = 13.sp,
                maxLines = 3,
                lineHeight = 18.sp,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(bottom = 16.dp)
            )

            // Buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // VER AHORA (Play) button
                Button(
                    onClick = onPlayClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD0BCFF)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .tvFocusable(shape = RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color(0xFF381E72),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "VER AHORA",
                        color = Color(0xFF381E72),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // DETALLES button
                Button(
                    onClick = onDetailsClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF49454F)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .tvFocusable(shape = RoundedCornerShape(8.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFFE6E1E5),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MÁS DETALLES",
                        color = Color(0xFFE6E1E5),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
