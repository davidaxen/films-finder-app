package com.darvi.filmhunter.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.entity.WatchProvider
import com.darvi.filmhunter.domain.usecase.movie.GetMoviesList
import com.darvi.filmhunter.domain.usecase.movie.SearchMoviesByGenres
import com.darvi.filmhunter.domain.usecase.movie.SearchMoviesByTitle
import com.darvi.filmhunter.domain.usecase.series.GetSeriesList
import com.darvi.filmhunter.domain.usecase.series.SearchSeriesByGenres
import com.darvi.filmhunter.domain.usecase.series.SearchSeriesByTitle
import com.darvi.filmhunter.presentation.core.model.FilmType
import com.darvi.filmhunter.presentation.core.model.FilmUiModel
import com.darvi.filmhunter.presentation.core.model.MovieListSection
import com.darvi.filmhunter.presentation.core.model.SearchMethod
import com.darvi.filmhunter.presentation.core.model.SeriesListSection
import com.darvi.filmhunter.presentation.core.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultListViewModel @Inject constructor(
    private val searchMoviesByTitle: SearchMoviesByTitle,
    private val searchMoviesByGenres: SearchMoviesByGenres,
    private val searchSeriesByTitle: SearchSeriesByTitle,
    private val searchSeriesByGenres: SearchSeriesByGenres,
    private val getMoviesList: GetMoviesList,
    private val getSeriesList: GetSeriesList
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultListUiState())
    val uiState: StateFlow<ResultListUiState> = _uiState

    fun onQueryParamsLoad(
        q: String,
        filmType: Int
    ) {
        if (q.isNotEmpty()) {
            val type = if (filmType == FilmType.SERIES.value) FilmType.SERIES else FilmType.MOVIE
            _uiState.update {
                it.copy(
                    searchQuery = q,
                    isSearching = true,
                    isLoadingMore = false,
                    endReached = false,
                    currentPage = 1,
                    filmTypeSelected = type,
                    searchMethod = SearchMethod.TITLE,
                    filmsFound = emptyList()
                )
            }
            loadPage(page = 1, isFirstPage = true)
        }
    }

    fun onGenresParamsLoad(
        genre: Int,
        platformId: Int?,
        filmType: Int
    ) {
        val type = if (filmType == FilmType.SERIES.value) FilmType.SERIES else FilmType.MOVIE
        _uiState.update {
            it.copy(
                genreIdSelected = genre,
                platformSelected = WatchProvider.fromId(platformId),
                isSearching = true,
                isLoadingMore = false,
                endReached = false,
                currentPage = 1,
                filmTypeSelected = type,
                searchMethod = SearchMethod.GENRE,
                filmsFound = emptyList()
            )
        }
        loadPage(page = 1, isFirstPage = true)
    }

    fun onSearchMethodParamsLoad(
        searchMethod: String,
        filmType: Int
    ) {
        if (searchMethod.isNotEmpty()) {
            val type = if (filmType == FilmType.SERIES.value) FilmType.SERIES else FilmType.MOVIE
            _uiState.update {
                it.copy(
                    isSearching = true,
                    isLoadingMore = false,
                    endReached = false,
                    currentPage = 1,
                    filmTypeSelected = type,
                    searchMethod = SearchMethod.fromValue(searchMethod) ?: SearchMethod.POPULAR,
                    filmsFound = emptyList()
                )
            }
            loadPage(page = 1, isFirstPage = true)
        }
    }

    fun loadNextPage() {
        if (_uiState.value.isLoadingMore || _uiState.value.endReached || _uiState.value.isSearching) return
        loadPage(page = _uiState.value.currentPage, isFirstPage = false)
    }

    private fun loadPage(page: Int, isFirstPage: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isFirstPage) {
                _uiState.update { it.copy(isSearching = true) }
            } else {
                _uiState.update { it.copy(isLoadingMore = true) }
            }

            val state = _uiState.value

            val newFilmsList = when (state.searchMethod) {
                SearchMethod.TITLE -> when (state.filmTypeSelected) {
                    FilmType.MOVIE -> searchMoviesByTitle(
                        state.searchQuery,
                        page
                    ).map { it.toUiModel() }

                    FilmType.SERIES -> searchSeriesByTitle(
                        state.searchQuery,
                        page
                    ).map { it.toUiModel() }
                }

                SearchMethod.GENRE -> {
                    if (state.genreIdSelected != null) {
                        val platform = if (state.platformSelected != null) {
                            state.platformSelected.id.toString()
                        } else {
                            ""
                        }
                        when (state.filmTypeSelected) {
                            FilmType.MOVIE -> searchMoviesByGenres(
                                state.genreIdSelected.toString(),
                                platform,
                                page
                            ).map { it.toUiModel() }

                            FilmType.SERIES -> searchSeriesByGenres(
                                state.genreIdSelected.toString(),
                                platform,
                                page
                            ).map { it.toUiModel() }
                        }
                    } else {
                        emptyList()
                    }
                }

                else -> when (state.filmTypeSelected) {
                    FilmType.MOVIE -> getMoviesList(
                        MovieListSection.fromSearchMethod(state.searchMethod.value).path,
                        page
                    ).map { it.toUiModel() }

                    FilmType.SERIES -> getSeriesList(
                        SeriesListSection.fromSearchMethod(state.searchMethod.value).path,
                        page
                    ).map { it.toUiModel() }
                }
            }


            _uiState.update { state ->
                val allMovies =
                    (if (isFirstPage) newFilmsList else state.filmsFound + newFilmsList).distinctBy { it.id }

                state.copy(
                    filmsFound = allMovies,
                    isSearching = false,
                    isLoadingMore = false,
                    endReached = newFilmsList.isEmpty(),
                    currentPage = if (newFilmsList.isNotEmpty()) page + 1 else state.currentPage
                )
            }
        }
    }
}

data class ResultListUiState(
    val searchQuery: String = "",
    val genreIdSelected: Int? = null,
    val platformSelected: WatchProvider? = null,
    val isSearching: Boolean = false,
    val isLoadingMore: Boolean = false,
    val endReached: Boolean = false,
    val currentPage: Int = 1,
    val filmsFound: List<FilmUiModel> = emptyList(),
    val filmTypeSelected: FilmType = FilmType.MOVIE,
    val searchMethod: SearchMethod = SearchMethod.TITLE
)