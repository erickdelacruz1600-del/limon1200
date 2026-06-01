package com.example.data

import android.util.Log
import com.example.model.Movie
import com.example.model.SubtitleTrack
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class TmdbMovie(
    val id: Long,
    val title: String?,
    val name: String?,
    val overview: String?,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "vote_average") val voteAverage: Double?,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "first_air_date") val firstAirDate: String?,
    @Json(name = "genre_ids") val genreIds: List<Int>?
)

@JsonClass(generateAdapter = true)
data class TmdbResponse(
    val results: List<TmdbMovie>?
)

interface TmdbApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES",
        @Query("page") page: Int = 1
    ): TmdbResponse

    @GET("tv/popular")
    suspend fun getPopularSeries(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES",
        @Query("page") page: Int = 1
    ): TmdbResponse

    @GET("discover/tv")
    suspend fun getAnimes(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES",
        @Query("with_genres") withGenres: String = "16", // 16 = Animation
        @Query("with_original_language") originalLanguage: String = "ja",
        @Query("page") page: Int = 1
    ): TmdbResponse
}

object RetrofitClient {
    private val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val apiService: TmdbApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TmdbApiService::class.java)
    }
}

object TmdbMapper {
    private val sampleVideos = listOf(
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
    )

    private val sampleSubtitles = listOf(
        SubtitleTrack("Español Latino (ES)", "es", "https://raw.githubusercontent.com/andrey-utkin/vtt-test/master/test.vtt"),
        SubtitleTrack("English Sub (EN)", "en", "https://raw.githubusercontent.com/yinying/webvtt-test/master/vtt/subtitles.vtt")
    )

    fun mapToMovie(tmdbMovie: TmdbMovie, category: String, index: Int): Movie {
        val titleClean = tmdbMovie.title ?: tmdbMovie.name ?: "Sin Título"
        val descClean = if (tmdbMovie.overview.isNullOrBlank()) {
            "No hay descripción disponible para esta entrega en el servicio de TMDB."
        } else {
            tmdbMovie.overview
        }

        val posterUrl = if (!tmdbMovie.posterPath.isNullOrEmpty()) {
            "https://image.tmdb.org/t/p/w500${tmdbMovie.posterPath}"
        } else {
            "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=500&auto=format&fit=crop&q=80"
        }

        val backdropUrl = if (!tmdbMovie.backdropPath.isNullOrEmpty()) {
            "https://image.tmdb.org/t/p/w1280${tmdbMovie.backdropPath}"
        } else {
            "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=1080&auto=format&fit=crop&q=80"
        }

        val ratingClean = tmdbMovie.voteAverage ?: 7.5
        val yearParsed = parseYear(tmdbMovie.releaseDate ?: tmdbMovie.firstAirDate)
        val durationClean = generateDuration(category, index)
        val genresMapped = mapGenreIds(tmdbMovie.genreIds, category != "Películas")

        return Movie(
            id = "tmdb_${category.lowercase()}_${tmdbMovie.id}",
            title = titleClean,
            description = descClean,
            videoUrl = sampleVideos[index % sampleVideos.size],
            backdropUrl = backdropUrl,
            posterUrl = posterUrl,
            rating = ratingClean,
            year = yearParsed,
            duration = durationClean,
            genres = if (genresMapped.isEmpty()) listOf(category) else genresMapped,
            category = category,
            subtitles = sampleSubtitles
        )
    }

    private fun parseYear(dateStr: String?): Int {
        if (dateStr == null || dateStr.length < 4) return 2025
        return try {
            dateStr.substring(0, 4).toInt()
        } catch (e: Exception) {
            2025
        }
    }

    private fun generateDuration(category: String, index: Int): String {
        return when (category) {
            "Películas" -> {
                val runtimes = listOf("2h 14m", "1h 56m", "2h 35m", "1h 48m", "2h 05m")
                runtimes[index % runtimes.size]
            }
            else -> {
                val runs = listOf("12 Episodios", "Temporada 1", "Temporada 2", "10 Episodios")
                runs[index % runs.size]
            }
        }
    }

    private fun mapGenreIds(genreIds: List<Int>?, isTv: Boolean): List<String> {
        if (genreIds == null) return emptyList()
        val genresMap = mapOf(
            28 to "Acción",
            12 to "Aventura",
            16 to "Animación",
            35 to "Comedia",
            80 to "Crimen",
            99 to "Documental",
            18 to "Drama",
            10751 to "Familiar",
            14 to "Fantasía",
            36 to "Historia",
            27 to "Terror",
            10402 to "Música",
            9648 to "Misterio",
            10749 to "Romance",
            878 to "Ciencia Ficción",
            53 to "Suspense",
            10752 to "Bélica",
            37 to "Western",
            10759 to "Acción y Aventura",
            10762 to "Kids",
            10765 to "Sci-Fi & Fantasy",
            10768 to "War & Politics"
        )
        return genreIds.mapNotNull { genresMap[it] }.take(3)
    }
}
