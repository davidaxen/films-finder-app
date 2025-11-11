package com.darvi.filmhunter.data.di

import com.darvi.filmhunter.data.datasource.SupabaseAuthDataSource
import com.darvi.filmhunter.data.datasource.SupabaseAuthDataSourceImpl
import com.darvi.filmhunter.data.repository.AuthRepositoryImpl
import com.darvi.filmhunter.data.repository.MovieRepositoryImpl
import com.darvi.filmhunter.data.repository.SessionRepositoryImpl
import com.darvi.filmhunter.data.datasource.api.ApiService
import com.darvi.filmhunter.data.repository.SeriesRepositoryImpl
import com.darvi.filmhunter.domain.repository.AuthRepository
import com.darvi.filmhunter.domain.repository.MovieRepository
import com.darvi.filmhunter.domain.repository.SeriesRepository
import com.darvi.filmhunter.domain.repository.SessionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.auth.Auth
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
    fun provideSeriesRepository(api: ApiService): SeriesRepository {
        return SeriesRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideMovieRepository(api: ApiService): MovieRepository {
        return MovieRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}