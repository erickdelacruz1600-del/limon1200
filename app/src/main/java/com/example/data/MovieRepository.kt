package com.example.data

import com.example.model.Movie
import com.example.model.SubtitleTrack

object MovieRepository {
    // Open source movies from the Blender Foundation & Google's Sample Videos, widely used for media player testing.
    var movies = listOf(
        Movie(
            id = "sintel",
            title = "Sintel",
            description = "Sintel es una conmovedora historia de fantasía y aventura. Sigue a una joven solitaria mientras busca incansablemente a su dragón bebé, enfrentando climas despiadados y monstruos gigantescos en un viaje espiritual inolvidable.",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
            backdropUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/Sintel.jpg",
            posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500&auto=format&fit=crop&q=80", // Premium fantasy mood poster
            rating = 8.8,
            year = 2010,
            duration = "14m 48s",
            genres = listOf("Fantasía", "Aventura", "Drama"),
            category = "Fantasía y Aventura",
            subtitles = listOf(
                SubtitleTrack("Español (ES)", "es", "https://raw.githubusercontent.com/andrey-utkin/vtt-test/master/test.vtt"),
                SubtitleTrack("English (EN)", "en", "https://raw.githubusercontent.com/yinying/webvtt-test/master/vtt/subtitles.vtt"),
                SubtitleTrack("Português (PT)", "pt", "https://raw.githubusercontent.com/andrey-utkin/vtt-test/master/test.vtt")
            )
        ),
        Movie(
            id = "tears-of-steel",
            title = "Tears of Steel",
            description = "En un Ámsterdam futurista de ciencia ficción, un grupo de científicos y programadores utilizan tecnología holográfica de vanguardia para recrear un evento del pasado e intentar salvar a la Tierra de una invasión inminente de robots gigantes.",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            backdropUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/TearsOfSteel.jpg",
            posterUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=500&auto=format&fit=crop&q=80", // Premium sci-fi mood poster
            rating = 8.4,
            year = 2012,
            duration = "12m 14s",
            genres = listOf("Ciencia Ficción", "Acción", "Efectos Especiales"),
            category = "Acción y Ciencia Ficción",
            subtitles = listOf(
                SubtitleTrack("Español (ES)", "es", "https://raw.githubusercontent.com/andrey-utkin/vtt-test/master/test.vtt"),
                SubtitleTrack("English (EN)", "en", "https://raw.githubusercontent.com/yinying/webvtt-test/master/vtt/subtitles.vtt")
            )
        ),
        Movie(
            id = "big-buck-bunny",
            title = "Big Buck Bunny",
            description = "Un entrañable conejo gigante de corazón noble decide tomar cartas en el asunto cuando un trío de ardillas tramposas y caprichosas arruinan su jardín de flores predilectas y molestan a los indefensos seres del bosque.",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            backdropUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg",
            posterUrl = "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=500&auto=format&fit=crop&q=80", // Premium cartoon book mood poster
            rating = 7.9,
            year = 2008,
            duration = "9m 56s",
            genres = listOf("Animación", "Comedia", "Familiar"),
            category = "Clásicos Animados",
            subtitles = listOf(
                SubtitleTrack("Español (ES)", "es", "https://raw.githubusercontent.com/andrey-utkin/vtt-test/master/test.vtt"),
                SubtitleTrack("English (EN)", "en", "https://raw.githubusercontent.com/yinying/webvtt-test/master/vtt/subtitles.vtt")
            )
        ),
        Movie(
            id = "elephants-dream",
            title = "Elephants Dream",
            description = "Un viaje surrealista a través de una maquinaria gigante de vapor donde Proog y Emo, dos personajes opuestos en edad y mentalidad, exploran los confines físicos e interpretativos de un mundo metálico automatizado de fantasía.",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            backdropUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ElephantsDream.jpg",
            posterUrl = "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=500&auto=format&fit=crop&q=80", // Premium surreal mechanical poster
            rating = 7.5,
            year = 2006,
            duration = "10m 53s",
            genres = listOf("Fantasía", "Ciencia Ficción", "Experimental"),
            category = "Acción y Ciencia Ficción",
            subtitles = listOf(
                SubtitleTrack("Español (ES)", "es", "https://raw.githubusercontent.com/andrey-utkin/vtt-test/master/test.vtt")
            )
        ),
        Movie(
            id = "sub_hight_def_test",
            title = "Cine HD Test (Subtitles)",
            description = "Cortometraje de demostración técnica de reproducción en alta definición de video y renderización de subtítulos sincronizados, ideal para verificar la calidad de imagen HDR, balance de colores y fluidez de reproducción en pantallas de alta gama TCL.",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            backdropUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerBlazes.jpg",
            posterUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=500&auto=format&fit=crop&q=80", // Premium neon movie lens poster
            rating = 9.2,
            year = 2025,
            duration = "15m 00s",
            genres = listOf("Test Técnico", "HD 1080p", "Cinema"),
            category = "Clásicos Animados",
            subtitles = listOf(
                SubtitleTrack("Español Latino (ES)", "es", "https://raw.githubusercontent.com/andrey-utkin/vtt-test/master/test.vtt"),
                SubtitleTrack("English Sub (EN)", "en", "https://raw.githubusercontent.com/yinying/webvtt-test/master/vtt/subtitles.vtt")
            )
        )
    )

    fun getMovieById(id: String): Movie? {
        return movies.find { it.id == id }
    }

    fun getMoviesByCategory(category: String): List<Movie> {
        return movies.filter { it.category == category }
    }

    fun getAllCategories(): List<String> {
        return movies.map { it.category }.distinct()
    }
}
