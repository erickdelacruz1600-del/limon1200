package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MovieRepository
import com.example.data.RetrofitClient
import com.example.data.TmdbMapper
import com.example.model.Movie
import com.example.model.SubtitleTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    data class Details(val movie: Movie) : Screen()
    data class Player(val movie: Movie, val selectedSubtitle: SubtitleTrack?) : Screen()
}

class MovieViewModel : ViewModel() {
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    // Subtitle Customization settings (Small: 18f, Medium: 24f, Large: 32f, Giant: 42f)
    private val _subtitleSize = MutableStateFlow(24f) 
    val subtitleSize: StateFlow<Float> = _subtitleSize.asStateFlow()

    private val _subtitleColor = MutableStateFlow(0xFFFFFF00) // TV default is Yellow or White for high accessibility contrast
    val subtitleColor: StateFlow<Long> = _subtitleColor.asStateFlow()

    // Dynamic Movies State Flow
    private val _moviesState = MutableStateFlow<List<Movie>>(MovieRepository.movies)
    val moviesState: StateFlow<List<Movie>> = _moviesState.asStateFlow()

    // Loading and Error Status State Flow
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadTmdbContent()
    }

    fun loadTmdbContent() {
        viewModelScope.launch {
            var apiKey = com.example.BuildConfig.TMDB_API_KEY
            
            // Fallback to the user's specific TMDB key if build config is empty or placeholder
            if (apiKey.isBlank() || apiKey == "YOUR_TMDB_API_KEY" || apiKey == "MY_TMDB_API_KEY") {
                apiKey = "3133a62b9232b181f8cc5b9cb990b994"
            }

            _isLoading.value = true
            _errorMessage.value = null
            try {
                Log.d("MovieViewModel", "Fetching popular movies, series, and anime with TMDB key.")
                
                // Fetch Movies
                val moviesResponse = RetrofitClient.apiService.getPopularMovies(apiKey)
                val fetchedMovies = moviesResponse.results?.mapIndexed { index, tmdbMovie ->
                    TmdbMapper.mapToMovie(tmdbMovie, "Películas", index)
                } ?: emptyList()

                // Fetch Series
                val seriesResponse = RetrofitClient.apiService.getPopularSeries(apiKey)
                val fetchedSeries = seriesResponse.results?.mapIndexed { index, tmdbMovie ->
                    TmdbMapper.mapToMovie(tmdbMovie, "Series", index)
                } ?: emptyList()

                // Fetch Anime
                val animeResponse = RetrofitClient.apiService.getAnimes(apiKey)
                val fetchedAnime = animeResponse.results?.mapIndexed { index, tmdbMovie ->
                    TmdbMapper.mapToMovie(tmdbMovie, "Anime", index)
                } ?: emptyList()

                // Combine all loaded content
                val allMedia = fetchedMovies + fetchedSeries + fetchedAnime

                if (allMedia.isNotEmpty()) {
                    // Update repository and StateFlow
                    MovieRepository.movies = allMedia
                    _moviesState.value = allMedia
                    Log.d("MovieViewModel", "Successfully loaded TMDB content: ${allMedia.size} items.")
                } else {
                    Log.w("MovieViewModel", "TMDB returned empty results, keeping local backups.")
                }

            } catch (e: Exception) {
                Log.e("MovieViewModel", "Failed to load from TMDB API, gracefully fallback to local movies.", e)
                _errorMessage.value = "Imposible conectar con TMDB (${e.localizedMessage ?: e.message}). Mostrando catálogo sin conexión."
                // Maintain default/already loaded content
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun toggleFavorite(movieId: String) {
        val current = _favorites.value
        _favorites.value = if (current.contains(movieId)) {
            current - movieId
        } else {
            current + movieId
        }
    }

    fun updateSubtitleSize(size: Float) {
        _subtitleSize.value = size
    }

    fun updateSubtitleColor(color: Long) {
        _subtitleColor.value = color
    }
}
