package com.darvi.filmhunter.data.model

import com.darvi.filmhunter.domain.entity.UserEntity

data class UserModel(
    val uid: String,
    val email: String,
    val displayName: String
)

fun UserModel.toDomain(): UserEntity {
    return UserEntity(
        id = uid,
        email = email,
        name = displayName
    )
}