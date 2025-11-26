package com.darvi.filmhunter.data.repository

import com.darvi.filmhunter.data.datasource.SupabaseDatabaseDataSource
import com.darvi.filmhunter.data.datasource.api.MovieApiService
import com.darvi.filmhunter.data.model.movie.toDomain
import com.darvi.filmhunter.domain.entity.movie.MovieDetailEntity
import com.darvi.filmhunter.domain.entity.movie.MovieEntity
import com.darvi.filmhunter.domain.repository.MovieRepository
import com.darvi.filmhunter.presentation.core.model.FilmType
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class MovieRepositoryImpl @Inject constructor(
    private val api: MovieApiService,
    private val database: SupabaseDatabaseDataSource
): MovieRepository {
    override suspend fun getMoviesList(path: String): List<MovieEntity> {
        return api.getMoviesList(path = path).results.map {
            it.toDomain()
        }
    }

    override suspend fun getMoviesByTitle(query: String, page: Int): List<MovieEntity> {
        return api.getMoviesByTitle(q = query, page = page).results.map {
            it.toDomain()
        }
    }

    override suspend fun getMoviesByGenres(
        genres: String,
        page: Int
    ): List<MovieEntity> {
        return api.getMoviesByGenres(genres = genres, page = page).results.map {
            it.toDomain()
        }
    }

    override suspend fun getMovieById(id: Int): MovieDetailEntity {
        return api
            .getMovieById(id = id)
            .toDomain()
            .copy(
                isSaved = database.isFilmSaved(id, FilmType.MOVIE.name)
            )
    }

    override suspend fun saveFilm(filmId: Int) {
        database.saveFilm(filmId, FilmType.MOVIE.name)
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun getSavedFilms(): List<MovieDetailEntity> {
        val filmsId = database.getSavedFilmsId(FilmType.MOVIE.name)

        return filmsId.map {
            api.getMovieById(id = it.filmId).toDomain().copy(
                isSaved = true,
                savedAt = Instant.parse(it.createdAt as String).toEpochMilliseconds()
            )
        }
    }

}