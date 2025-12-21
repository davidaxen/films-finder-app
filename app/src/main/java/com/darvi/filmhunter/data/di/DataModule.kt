package com.darvi.filmhunter.data.di

import com.darvi.filmhunter.data.datasource.SupabaseAuthDataSource
import com.darvi.filmhunter.data.datasource.SupabaseAuthDataSourceImpl
import com.darvi.filmhunter.data.datasource.SupabaseDatabaseDataSource
import com.darvi.filmhunter.data.datasource.SupabaseDatabaseDataSourceImpl
import com.darvi.filmhunter.data.repository.AuthRepositoryImpl
import com.darvi.filmhunter.data.repository.MovieRepositoryImpl
import com.darvi.filmhunter.data.repository.SessionRepositoryImpl
import com.darvi.filmhunter.data.datasource.api.MovieApiService
import com.darvi.filmhunter.data.datasource.api.SeriesApiService
import com.darvi.filmhunter.data.repository.SeriesRepositoryImpl
import com.darvi.filmhunter.data.repository.MatcherSessionRepositoryImpl
import com.darvi.filmhunter.domain.repository.AuthRepository
import com.darvi.filmhunter.domain.repository.MatcherSessionRepository
import com.darvi.filmhunter.domain.repository.MovieRepository
import com.darvi.filmhunter.domain.repository.SeriesRepository
import com.darvi.filmhunter.domain.repository.SessionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideSessionRepository(dataSource: SupabaseAuthDataSource): SessionRepository {
        return SessionRepositoryImpl(dataSource)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(dataSource: SupabaseAuthDataSource): AuthRepository {
        return AuthRepositoryImpl(dataSource)
    }

    @Provides
    @Singleton
    fun provideSupabaseAuthDataSource(supabaseAuth: Auth): SupabaseAuthDataSource {
        return SupabaseAuthDataSourceImpl(supabaseAuth)
    }

    @Provides
    @Singleton
    fun provideSupabaseDatabaseDataSource(database: Postgrest): SupabaseDatabaseDataSource {
        return SupabaseDatabaseDataSourceImpl(database)
    }

    @Provides
    @Singleton
    fun provideMatcherSessionRepository(database: SupabaseDatabaseDataSource): MatcherSessionRepository {
        return MatcherSessionRepositoryImpl(database)
    }

    @Provides
    @Singleton
    fun provideSeriesRepository(api: SeriesApiService, database: SupabaseDatabaseDataSource): SeriesRepository {
        return SeriesRepositoryImpl(api, database)
    }

    @Provides
    @Singleton
    fun provideMovieRepository(api: MovieApiService, database: SupabaseDatabaseDataSource): MovieRepository {
        return MovieRepositoryImpl(api, database)
    }

    @Provides
    @Singleton
    fun provideMovieApiService(retrofit: Retrofit): MovieApiService {
        return retrofit.create(MovieApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSeriesApiService(retrofit: Retrofit): SeriesApiService {
        return retrofit.create(SeriesApiService::class.java)
    }
}