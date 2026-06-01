package com.example.ui.screens

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.model.Movie
import com.example.model.SubtitleTrack
import com.example.ui.MovieViewModel
import com.example.ui.components.tvFocusable
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
    movie: Movie,
    selectedSubtitle: SubtitleTrack?,
    viewModel: MovieViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val subtitleSize by viewModel.subtitleSize.collectAsState()
    val subtitleColor by viewModel.subtitleColor.collectAsState()

    // 1. Initialize ExoPlayer with streaming HD video track
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true

            // Set subtitle configurations (WebVTT formats)
            val subtitleConfig = selectedSubtitle?.let { track ->
                MediaItem.SubtitleConfiguration.Builder(Uri.parse(track.url))
                    .setMimeType("text/vtt")
                    .setLanguage(track.code)
                    .setSelectionFlags(1) // Default selection
                    .build()
            }

            val mediaItem = MediaItem.Builder()
                .setUri(Uri.parse(movie.videoUrl))
                .apply {
                    if (subtitleConfig != null) {
                        setSubtitleConfigurations(listOf(subtitleConfig))
                    }
                }
                .build()

            setMediaItem(mediaItem)
            prepare()
        }
    }

    // 2. Playback State tracking
    var isPlaying by remember { mutableStateOf(true) }
    var currentPosition by remember { mutableStateOf(0L) }
    var totalDuration by remember { mutableStateOf(0L) }

    // 3. Remote Overlay visibility timer
    var showOverlay by remember { mutableStateOf(true) }

    // Synchronize playback timeline data and player state changes
    LaunchedEffect(exoPlayer) {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                totalDuration = exoPlayer.duration.coerceAtLeast(0L)
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        })

        while (true) {
            currentPosition = exoPlayer.currentPosition.coerceAtLeast(0L)
            if (totalDuration == 0L && exoPlayer.duration > 0) {
                totalDuration = exoPlayer.duration
            }
            delay(1000)
        }
    }

    // Automatic overlay removal after 5 seconds of inactivity on TV
    LaunchedEffect(showOverlay, isPlaying) {
        if (showOverlay && isPlaying) {
            delay(5000)
            showOverlay = false
        }
    }

    // Release player safely
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Compose Viewport layout
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { showOverlay = !showOverlay }
    ) {
        // - REAL EXOPLAYER SURFACE (With dynamic subtitle customized overrides!)
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false // Hide redundant mobile controls for custom TV focus layer
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = { playerView ->
                // Programmatically inject and style Media3 subtitles based on ViewModel configurations!
                playerView.subtitleView?.let { subtitleView ->
                    subtitleView.setFixedTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, subtitleSize)
                    subtitleView.setStyle(
                        androidx.media3.ui.CaptionStyleCompat(
                            subtitleColor.toInt(),
                            0x99000000.toInt(), // Dark drop background card
                            0,
                            androidx.media3.ui.CaptionStyleCompat.EDGE_TYPE_OUTLINE,
                            android.graphics.Color.BLACK,
                            null
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // - TELEVISION DPAD REMOTE NAVIGATION CONTROLLERS
        AnimatedVisibility(
            visible = showOverlay,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
            ) {
                // Top Header Row (Back button, Metadata details)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                .tvFocusable(shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = movie.title,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "STREAM 4K ULTRA HD • ",
                                    color = Color(0xFFD0BCFF),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = selectedSubtitle?.let { "SUBTÍTULOS: ${it.language.uppercase()}" } ?: "MUTE (SIN SUBTÍTULOS)",
                                    color = Color(0xFFCAC4D0),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Direct Subtitle scaling from player hud (Very comfortable on TCL TVs!)
                    Row(
                        modifier = Modifier
                            .tvFocusable(shape = RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                            .clickable {
                                val nextSize = when (subtitleSize) {
                                    18f -> 24f
                                    24f -> 32f
                                    32f -> 42f
                                    else -> 18f
                                }
                                viewModel.updateSubtitleSize(nextSize)
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configuración",
                            tint = Color(0xFFD0BCFF),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tamaño: ${subtitleSize.toInt()}px",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Middle Media player control wheel (DPAD optimized)
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rewind button (-10s)
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.Black.copy(alpha = 0.8f), CircleShape)
                            .tvFocusable(shape = CircleShape)
                            .clickable {
                                exoPlayer.seekTo((exoPlayer.currentPosition - 10000).coerceAtLeast(0L))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "-10s",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Dual Play / Pause remote key
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color(0xFFD0BCFF), CircleShape)
                            .tvFocusable(shape = CircleShape)
                            .clickable {
                                if (isPlaying) exoPlayer.pause() else exoPlayer.play()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isPlaying) {
                            // Custom high-quality Pause bars (No external icon dependency)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.wrapContentSize()
                            ) {
                                Box(modifier = Modifier.width(6.dp).height(22.dp).background(Color(0xFF381E72), RoundedCornerShape(2.dp)))
                                Box(modifier = Modifier.width(6.dp).height(22.dp).background(Color(0xFF381E72), RoundedCornerShape(2.dp)))
                            }
                        } else {
                            // Play Arrow
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Reproducir",
                                tint = Color(0xFF381E72),
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }

                    // Forward button (+10s)
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.Black.copy(alpha = 0.8f), CircleShape)
                            .tvFocusable(shape = CircleShape)
                            .clickable {
                                exoPlayer.seekTo((exoPlayer.currentPosition + 10000).coerceAtMost(totalDuration))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+10s",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Bottom Progress Tracker Bar
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    val progress = if (totalDuration > 0) currentPosition.toFloat() / totalDuration.toFloat() else 0f

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(3.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxHeight()
                                .background(Color(0xFFD0BCFF), RoundedCornerShape(3.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(currentPosition),
                            color = Color(0xFFCAC4D0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatTime(totalDuration),
                            color = Color(0xFFCAC4D0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
