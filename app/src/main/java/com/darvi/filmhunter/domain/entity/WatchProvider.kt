package com.darvi.filmhunter.domain.entity

enum class WatchProvider(val id: Int, val title: String, val logoPath: String) {
    NETFLIX(
        id = 8,
        title = "Netflix",
        logoPath = "/pbpMk2JmcoNnQwx5JGpXngfoWtp.jpg"
    ),
    DISNEY_PLUS(
        id = 337,
        title = "Disney Plus",
        logoPath = "/97yvRBw1GzX7fXprcF80er19ot.jpg"
    ),
    HBO_MAX(
        id = 1899,
        title = "HBO Max",
        logoPath = "/jbe4gVSfRlbPTdESXhEKpornsfu.jpg"
    )
}