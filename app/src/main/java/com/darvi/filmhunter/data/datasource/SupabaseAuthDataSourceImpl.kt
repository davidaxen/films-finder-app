package com.darvi.filmhunter.data.datasource

import com.darvi.filmhunter.data.model.UserModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class SupabaseAuthDataSourceImpl @Inject constructor(
    private val auth: Auth
) : SupabaseAuthDataSource {

    override fun getCurrentUser(): UserModel? {
        val user = auth.currentUserOrNull()
        return if (user == null) {
            null
        } else {
            UserModel(
                uid = user.id,
                email = user.email ?: "",
                displayName = (user.userMetadata?.get("name") ?: "") as String
            )
        }
    }

    override fun getCurrentUserFlow(): StateFlow<SessionStatus> {
        return auth.sessionStatus
    }

    override suspend fun signIn(
        email: String,
        password: String
    ): Result<UserModel> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val user = auth.currentSessionOrNull()?.user
                ?: return Result.failure(Exception("Usuario no encontrado"))

            return Result.success(
                UserModel(
                    uid = user.id,
                    email = user.email ?: email,
                    displayName = (user.userMetadata?.get("name") ?: "") as String
                )
            )
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(
        email: String,
        password: String
    ): UserModel {
        try {
            val result = auth.signUpWith(Email) {
                this.email = email
                this.password = password
            } ?: throw IllegalStateException("El usuario devuelto es nulo")

            return UserModel(
                uid = result.id,
                email = result.email ?: email,
                displayName = (result.userMetadata?.get("name") ?: "") as String
            )
        } catch (e: Throwable) {
            throw Exception("Error al registrar usuario: ${e.message}", e)
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

}