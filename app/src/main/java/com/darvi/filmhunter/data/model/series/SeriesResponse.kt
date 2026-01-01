package com.darvi.filmhunter.data.model.series

import com.darvi.filmhunter.domain.entity.series.SeriesEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeriesResponse(
    val results: List<SeriesModel>,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_results")
    val totalResults: Int
)

@Serializable
data class SeriesModel(
    val id: Int,
    @SerialName("name")
    val title: String,
    val overview: String,
    @SerialName("poster_path")
    val posterPath: String?,
    @SerialName("first_air_date")
    val releaseDate: String,
    @SerialName("vote_average")
    val voteAverage: Double
)

fun SeriesModel.toDomain(): SeriesEntity {
    return SeriesEntity(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage
    )
}
