package com.darvi.filmhunter.data.di

import com.darvi.filmhunter.BuildConfig
import com.darvi.filmhunter.data.datasource.SupabaseAuthDataSource
import com.darvi.filmhunter.data.datasource.SupabaseAuthDataSourceImpl
import com.darvi.filmhunter.data.repository.AuthRepositoryImpl
import com.darvi.filmhunter.data.repository.MovieRepositoryImpl
import com.darvi.filmhunter.data.repository.SessionRepositoryImpl
import com.darvi.filmhunter.data.service.api.ApiService
import com.darvi.filmhunter.domain.repository.AuthRepository
import com.darvi.filmhunter.domain.repository.MovieRepository
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
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
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
    fun provideMovieRepository(api: ApiService): MovieRepository {
        return MovieRepositoryImpl(api)
    }

    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${BuildConfig.TMDB_API_KEY}")
                    .build()
                chain.proceed(newRequest)
            }
            .build()
    }

    @Provides
    fun provideRetrofit(json: Json, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json; charset=UTF8".toMediaType()))
            .build()
    }

    @Provides
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
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