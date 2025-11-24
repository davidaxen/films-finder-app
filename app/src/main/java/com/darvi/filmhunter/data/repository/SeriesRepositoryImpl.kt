package com.darvi.filmhunter.data.repository

import com.darvi.filmhunter.data.datasource.SupabaseDatabaseDataSource
import com.darvi.filmhunter.data.datasource.api.SeriesApiService
import com.darvi.filmhunter.data.model.series.toDomain
import com.darvi.filmhunter.domain.entity.series.SeasonDetailEntity
import com.darvi.filmhunter.domain.entity.series.SeriesDetailEntity
import com.darvi.filmhunter.domain.entity.series.SeriesEntity
import com.darvi.filmhunter.domain.repository.SeriesRepository
import com.darvi.filmhunter.presentation.core.model.FilmType
import javax.inject.Inject

class SeriesRepositoryImpl @Inject constructor(
    private val api: SeriesApiService,
    private val database: SupabaseDatabaseDataSource
): SeriesRepository {
    override suspend fun getSeriesList(path: String): List<SeriesEntity> {
        return api.getSeriesList(path = path).results.map {
            it.toDomain()
        }
    }

    override suspend fun getSeriesByTitle(query: String, page: Int): List<SeriesEntity> {
        return api.getSeriesByTitle(q = query, page = page).results.map {
            it.toDomain()
        }
    }

    override suspend fun getSeriesByGenres(
        genres: String,
        page: Int
    ): List<SeriesEntity> {
        return api.getSeriesByGenres(genres = genres, page = page).results.map {
            it.toDomain()
        }
    }

    override suspend fun getSeriesById(id: Int): SeriesDetailEntity {
        return api
            .getSeriesById(id = id)
            .toDomain()
            .copy(
                isSaved = database.isFilmSaved(id, FilmType.SERIES.name)
            )
    }

    override suspend fun getSeriesSeasonDetail(
        seriesId: Int,
        seasonNumber: Int
    ): SeasonDetailEntity {
        return api.getSeriesSeasonDetail(seriesId = seriesId, seasonNumber = seasonNumber).toDomain()
    }

    override suspend fun saveFilm(filmId: Int) {
        database.saveFilm(filmId, FilmType.SERIES.name)
    }
}