package com.darvi.filmhunter.data.repository

import com.darvi.filmhunter.data.datasource.SupabaseDatabaseDataSource
import com.darvi.filmhunter.data.datasource.api.MovieApiService
import com.darvi.filmhunter.data.model.movie.toDomain
import com.darvi.filmhunter.domain.entity.movie.MovieDetailEntity
import com.darvi.filmhunter.domain.entity.movie.MovieEntity
import com.darvi.filmhunter.domain.repository.MovieRepository
import com.darvi.filmhunter.presentation.core.model.FilmType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class MovieRepositoryImpl @Inject constructor(
    private val api: MovieApiService,
    private val database: SupabaseDatabaseDataSource
): MovieRepository {
    private val savedMoviesFlow = MutableStateFlow<List<MovieDetailEntity>>(emptyList())
    override fun getSavedMoviesFlow(): Flow<List<MovieDetailEntity>> = savedMoviesFlow.asStateFlow()

    init {
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            fetchSavedMovies()
        }
    }

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
        platforms: String,
        page: Int
    ): List<MovieEntity> {
        return if (platforms.isNotEmpty()) {
            api.getMoviesByPlatformAndGenres(genres = genres, platformsId = platforms, page = page).results.map {
                it.toDomain()
            }
        } else {
            api.getMoviesByGenres(genres = genres, page = page).results.map {
                it.toDomain()
            }
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
        fetchSavedMovies()
    }

    override suspend fun removeSavedFilm(filmId: Int) {
        database.removeSavedFilm(filmId, FilmType.MOVIE.name)
        fetchSavedMovies()
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun fetchSavedMovies() {
        val filmsId = database.getSavedFilmsId(FilmType.MOVIE.name)

        val movies = filmsId.map {
            api.getMovieById(id = it.filmId).toDomain().copy(
                isSaved = true,
                savedAt = Instant.parse(it.createdAt as String).toEpochMilliseconds()
            )
        }

        savedMoviesFlow.value = movies
    }
}