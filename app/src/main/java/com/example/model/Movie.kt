package com.example.model

data class SubtitleTrack(
    val language: String,
    val code: String,
    val url: String
)

data class Movie(
    val id: String,
    val title: String,
    val description: String,
    val videoUrl: String,
    val backdropUrl: String,
    val posterUrl: String,
    val rating: Double,
    val year: Int,
    val duration: String,
    val genres: List<String>,
    val category: String,
    val subtitles: List<SubtitleTrack> = emptyList()
)
