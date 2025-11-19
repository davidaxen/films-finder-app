package com.darvi.filmhunter.data.model

import com.darvi.filmhunter.domain.entity.MovieDetailEntity
import com.darvi.filmhunter.domain.entity.MovieGenre
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetailResponse(
    val id: Int,
    val title: String,
    val overview: String,
    val runtime: Int? = null,
    @SerialName("original_title") val originalTitle: String,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("release_date") val releaseDate: String,
    @SerialName("vote_average") val voteAverage: Double,
    @SerialName("vote_count") val voteCount: Int,
    @SerialName("genres") val genres: List<GenreIdModel>,
    @SerialName("watch/providers") val watchProviders: WatchProviderResponse,
)

fun MovieDetailResponse.toDomain(): MovieDetailEntity {
    val esProvider = watchProviders.results["ES"]

    val providers =
        (esProvider?.flatrate.orEmpty() +
                esProvider?.rent.orEmpty() +
                esProvider?.buy.orEmpty())
            .associateBy { it.providerId }
            .values
            .map { it.toDomain() }

    return MovieDetailEntity(
        id = id,
        title = title,
        overview = overview,
        runtime = runtime,
        originalTitle = originalTitle,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        genres = genres.mapNotNull { MovieGenre.fromId(it.id) },
        watchProviders = providers
    )
}