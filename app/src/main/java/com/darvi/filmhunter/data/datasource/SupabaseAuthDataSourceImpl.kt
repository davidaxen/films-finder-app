package com.darvi.filmhunter.data.datasource

import com.darvi.filmhunter.data.model.UserModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.builtin.Email
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

    override suspend fun signIn(
        email: String,
        password: String
    ): UserModel {
        try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val user = auth.currentUserOrNull()
                ?: throw Exception("No se pudo obtener el usuario actual tras el login.")


            return UserModel(
                uid = user.id,
                email = user.email ?: email,
                displayName = (user.userMetadata?.get("name") ?: "") as String
            )
        } catch (e: Throwable) {
            throw Exception("Error al iniciar sesión: ${e.message}", e)
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
            } ?: throw Exception("El usuario devuelto es nulo")

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