package com.darvi.filmhunter.domain.usecase.matcher

import android.util.Log
import com.darvi.filmhunter.domain.repository.MatcherSessionRepository
import com.darvi.filmhunter.domain.repository.MovieRepository
import com.darvi.filmhunter.domain.repository.SeriesRepository
import com.darvi.filmhunter.presentation.core.model.FilmType
import javax.inject.Inject

class FetchAndStoreSessionTitles @Inject constructor(
    private val movieRepository: MovieRepository,
    private val seriesRepository: SeriesRepository,
    private val matcherSessionRepository: MatcherSessionRepository
) {
    suspend operator fun invoke(
        sessionId: String,
        filmType: FilmType,
        genres: List<Int>,
        platforms: List<Int>?
    ): Result<Unit> {
        return try {
            val targetCount = 75
            val allTitles = mutableListOf<Pair<Long, String>>()
            var currentPage = 1
            var hasMorePages = true

            // Build genres string (pipe-separated)
            val genresString = genres.joinToString("|")
            
            // Build platforms string (pipe-separated)
            val platformsString = when {
                platforms == null || platforms.isEmpty() -> ""
                else -> platforms.joinToString("|")
            }

            Log.d("FetchAndStoreSessionTitles", "genresString: $genresString")
            Log.d("FetchAndStoreSessionTitles", "platformsString: $platformsString")

            while (allTitles.size < targetCount && hasMorePages) {
                val titles = if (filmType == FilmType.MOVIE) {
                    val movies = movieRepository.getMoviesByGenres(
                        genres = genresString,
                        platforms = platformsString,
                        page = currentPage
                    )
                    movies.map { movie ->
                        movie.id.toLong() to "movie"
                    }
                } else {
                    val series = seriesRepository.getSeriesByGenres(
                        genres = genresString,
                        platforms = platformsString,
                        page = currentPage
                    )
                    series.map { serie ->
                        serie.id.toLong() to "tv"
                    }
                }

                if (titles.isEmpty()) {
                    hasMorePages = false
                } else {
                    // Add titles up to target count
                    val remaining = targetCount - allTitles.size
                    if (remaining > 0) {
                        allTitles.addAll(titles.take(remaining))
                    }
                    currentPage++
                    
                    // Check if we've reached the target count
                    if (allTitles.size >= targetCount) {
                        break
                    }
                }
            }

            // Limit to exactly 75 titles
            val finalTitles = allTitles.take(targetCount)
            
            // Store titles in database
            matcherSessionRepository.insertSessionTitles(sessionId, finalTitles)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

