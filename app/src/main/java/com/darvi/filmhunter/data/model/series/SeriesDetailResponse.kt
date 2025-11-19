package com.darvi.filmhunter.data.model.series

import com.darvi.filmhunter.data.model.GenreIdModel
import com.darvi.filmhunter.data.model.WatchProviderResponse
import com.darvi.filmhunter.data.model.toDomain
import com.darvi.filmhunter.domain.entity.movie.MovieGenre
import com.darvi.filmhunter.domain.entity.series.SeriesDetailEntity
import com.darvi.filmhunter.domain.entity.series.SeriesSeasonEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeriesDetailResponse(
    val id: Int,
    @SerialName("name") val title: String,
    val overview: String,
    @SerialName("original_name") val originalTitle: String,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("first_air_date") val releaseDate: String,
    @SerialName("vote_average") val voteAverage: Double,
    @SerialName("vote_count") val voteCount: Int,
    @SerialName("genres") val genres: List<GenreIdModel>,
    @SerialName("seasons") val seasons: List<SeriesSeasonModel>,
    @SerialName("watch/providers") val watchProviders: WatchProviderResponse,
)

@Serializable
data class SeriesSeasonModel(
    val id: Int,
    val name: String,
    @SerialName("season_number") val seasonNumber: Int,
    @SerialName("episode_count") val episodeCount: Int,
    @SerialName("poster_path") val posterPath: String? = null,
)

fun SeriesDetailResponse.toDomain(): SeriesDetailEntity {
    val esProvider = watchProviders.results["ES"]

    val providers =
        (esProvider?.flatrate.orEmpty() +
                esProvider?.rent.orEmpty() +
                esProvider?.buy.orEmpty())
            .associateBy { it.providerId }
            .values
            .map { it.toDomain() }

    return SeriesDetailEntity(
        id = id,
        title = title,
        overview = overview,
        originalTitle = originalTitle,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        genres = genres.mapNotNull { MovieGenre.fromId(it.id) },
        seasons = seasons.map { it.toDomain() },
        watchProviders = providers
    )
}

fun SeriesSeasonModel.toDomain(): SeriesSeasonEntity {
    return SeriesSeasonEntity(
        id = id,
        name = name,
        seasonNumber = seasonNumber,
        episodeCount = episodeCount,
        posterPath = posterPath
    )
}