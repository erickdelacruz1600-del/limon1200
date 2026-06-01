package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.MovieViewModel
import com.example.ui.Screen
import com.example.ui.screens.DetailsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MovieViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Force darkTheme = true and dynamicColor = false to maintain 
            // a sleek, high-end high-contrast dark visual identity suited for living room TV environments.
            MyApplicationTheme(darkTheme = true, dynamicColor = false) {
                val currentScreen by viewModel.currentScreen.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFF070709) // Pure movie atmosphere black
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        when (val screen = currentScreen) {
                            is Screen.Home -> {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onMovieClick = { movie ->
                                        viewModel.navigateTo(Screen.Details(movie))
                                    }
                                )
                            }
                            is Screen.Details -> {
                                // DPAD Back key interception for Android TV
                                BackHandler {
                                    viewModel.navigateTo(Screen.Home)
                                }
                                DetailsScreen(
                                    movie = screen.movie,
                                    viewModel = viewModel,
                                    onBackClick = {
                                        viewModel.navigateTo(Screen.Home)
                                    }
                                )
                            }
                            is Screen.Player -> {
                                // DPAD Back key interception to close video and return to details
                                BackHandler {
                                    viewModel.navigateTo(Screen.Details(screen.movie))
                                }
                                PlayerScreen(
                                    movie = screen.movie,
                                    selectedSubtitle = screen.selectedSubtitle,
                                    viewModel = viewModel,
                                    onBackClick = {
                                        viewModel.navigateTo(Screen.Details(screen.movie))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
