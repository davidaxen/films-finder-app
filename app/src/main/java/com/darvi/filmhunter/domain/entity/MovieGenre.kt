package com.darvi.filmhunter.domain.entity

enum class MovieGenre(val id: Int, val displayName: String) {
    ACTION(28, "Acción"),
    ADVENTURE(12, "Aventura"),
    ANIMATION(16, "Animación"),
    COMEDY(35, "Comedia"),
    CRIME(80, "Crimen"),
    DOCUMENTARY(99, "Documental"),
    DRAMA(18, "Drama"),
    FAMILY(10751, "Familia"),
    FANTASY(14, "Fantasía"),
    HISTORY(36, "Historia"),
    HORROR(27, "Terror"),
    MUSIC(10402, "Música"),
    MYSTERY(9648, "Misterio"),
    ROMANCE(10749, "Romance"),
    SCIENCE_FICTION(878, "Ciencia ficción"),
    TV_MOVIE(10770, "Película de TV"),
    THRILLER(53, "Suspense"),
    WAR(10752, "Bélica"),
    WESTERN(37, "Western");

    companion object {
        fun fromId(id: Int): MovieGenre? =
            entries.firstOrNull { it.id == id }

        fun fromName(name: String): MovieGenre? =
            entries.firstOrNull { it.displayName == name }
    }
}