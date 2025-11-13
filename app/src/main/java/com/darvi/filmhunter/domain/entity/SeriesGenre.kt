package com.darvi.filmhunter.domain.entity

enum class SeriesGenre(val id: Int, val displayName: String) {
    ACTION_ADVENTURE(10759, "Action & Adventure"),
    ANIMATION(16, "Animación"),
    COMEDY(35, "Comedia"),
    CRIME(80, "Crimen"),
    DOCUMENTARY(99, "Documental"),
    DRAMA(18, "Drama"),
    FAMILY(10751, "Familia"),
    KIDS(10762, "Kids"),
    MYSTERY(9648, "Misterio"),
    NEWS(10763, "News"),
    REALITY(10764, "Reality"),
    SCI_FI_FANTASY(10765, "Sci-Fi & Fantasy"),
    SOAP(10766, "Soap"),
    TALK(10767, "Talk"),
    WAR_POLITICS(10768, "War & Politics"),
    WESTERN(37, "Western");

    companion object {
        fun fromId(id: Int): SeriesGenre? =
            entries.firstOrNull { it.id == id }

        fun fromName(name: String): SeriesGenre? =
            entries.firstOrNull { it.displayName == name }
    }
}