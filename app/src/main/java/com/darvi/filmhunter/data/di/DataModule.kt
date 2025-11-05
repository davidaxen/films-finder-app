package com.darvi.filmhunter.data.di

import com.darvi.filmhunter.BuildConfig
import com.darvi.filmhunter.data.datasource.SupabaseAuthDataSource
import com.darvi.filmhunter.data.datasource.SupabaseAuthDataSourceImpl
import com.darvi.filmhunter.data.repository.AuthRepositoryImpl
import com.darvi.filmhunter.data.repository.SessionRepositoryImpl
import com.darvi.filmhunter.domain.repository.AuthRepository
import com.darvi.filmhunter.domain.repository.SessionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun provideSessionRepository(dataSource: SupabaseAuthDataSource): SessionRepository {
        return SessionRepositoryImpl(dataSource)
    }

    @Provides
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
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            install(Postgrest)
            install(Auth)
        }
    }

    @Provides
    @Singleton
    fun provideSupabaseAuth(client: SupabaseClient): Auth {
        return client.auth
    }
}