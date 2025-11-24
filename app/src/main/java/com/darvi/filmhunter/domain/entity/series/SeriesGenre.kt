package com.darvi.filmhunter.domain.entity.series

enum class SeriesGenre(val id: Int, val displayName: String) {
    ACTION_ADVENTURE(10759, "Acción y Aventura"),
    ANIMATION(16, "Animación"),
    COMEDY(35, "Comedia"),
    CRIME(80, "Crimen"),
    DOCUMENTARY(99, "Documental"),
    DRAMA(18, "Drama"),
    FAMILY(10751, "Familia"),
    KIDS(10762, "Infantil"),
    MYSTERY(9648, "Misterio"),
    NEWS(10763, "Noticias"),
    REALITY(10764, "Reality"),
    SCI_FI_FANTASY(10765, "Ciencia Ficción"),
    SOAP(10766, "Telenovela"),
    TALK(10767, "Talk Show"),
    WAR_POLITICS(10768, "Guerra"),
    WESTERN(37, "Western");

    companion object {
        fun fromId(id: Int): SeriesGenre? =
            entries.firstOrNull { it.id == id }

        fun fromName(name: String): SeriesGenre? =
            entries.firstOrNull { it.displayName == name }
    }
}