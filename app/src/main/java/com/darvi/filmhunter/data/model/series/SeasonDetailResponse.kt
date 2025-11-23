package com.darvi.filmhunter.data.model.series

import com.darvi.filmhunter.data.model.WatchProviderResponse
import com.darvi.filmhunter.data.model.toDomain
import com.darvi.filmhunter.domain.entity.series.EpisodeEntity
import com.darvi.filmhunter.domain.entity.series.SeasonDetailEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.collections.associateBy
import kotlin.collections.orEmpty
import kotlin.collections.plus

@Serializable
data class SeasonDetailResponse(
    @SerialName("name") val title: String,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("air_date") val releaseDate: String?,
    @SerialName("vote_average") val voteAverage: Double,
    @SerialName("watch/providers") val watchProviders: WatchProviderResponse,
    val overview: String,
    val episodes: List<EpisodeModel>,
)

@Serializable
data class EpisodeModel(
    @SerialName("name") val title: String,
    val overview: String,
    val runtime: Int? = null,
    @SerialName("episode_number") val episodeNumber: Int,
    @SerialName("vote_average") val voteAverage: Double,
    @SerialName("vote_count") val voteCount: Int,
    @SerialName("air_date") val releaseDate: String?,
    @SerialName("still_path") val picturePath: String?,
)

fun SeasonDetailResponse.toDomain(): SeasonDetailEntity {
    val esProvider = watchProviders.results["ES"]

    val providers =
        (esProvider?.flatrate.orEmpty() +
                esProvider?.rent.orEmpty() +
                esProvider?.buy.orEmpty())
            .associateBy { it.providerId }
            .values
            .map { it.toDomain() }

    return SeasonDetailEntity(
        title = title,
        description = overview,
        posterPath = posterPath,
        releaseDate = releaseDate ?: "",
        voteAverage = voteAverage,
        episodes = episodes.map { it.toDomain() },
        watchProviders = providers
    )
}

fun EpisodeModel.toDomain(): EpisodeEntity {
    return EpisodeEntity(
        title = title,
        overview = overview,
        runtime = runtime,
        episodeNumber = episodeNumber,
        voteAverage = voteAverage,
        voteCount = voteCount,
        releaseDate = releaseDate ?: "",
        picturePath = picturePath
    )
}