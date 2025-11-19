package com.darvi.filmhunter.data.model

import com.darvi.filmhunter.domain.entity.WatchProviderEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WatchProviderResponse(
    val results: Map<String, CountryWatchProviders>
)

@Serializable
data class CountryWatchProviders(
    val flatrate: List<Provider>? = null,
    val rent: List<Provider>? = null,
    val buy: List<Provider>? = null
)

@Serializable
data class Provider(
    @SerialName("logo_path") val logoPath: String?,
    @SerialName("provider_id")val providerId: Int,
    @SerialName("provider_name")val providerName: String,
)

fun Provider.toDomain(): WatchProviderEntity {
    return WatchProviderEntity(
        id = providerId,
        name = providerName,
        logoPath = logoPath
    )
}