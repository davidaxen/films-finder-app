package com.darvi.filmhunter.data.datasource

import com.darvi.filmhunter.data.model.UserModel
import io.github.jan.supabase.SupabaseClient

class SupabaseAuthDataSourceImpl(
    supabaseAuth: SupabaseClient
) : SupabaseAuthDataSource {
    override suspend fun signIn(
        email: String,
        password: String
    ): UserModel {
    }

    override suspend fun signUp(
        email: String,
        password: String
    ): UserModel {
        return UserModel("","","")
    }

    override suspend fun signOut() {

    }

}