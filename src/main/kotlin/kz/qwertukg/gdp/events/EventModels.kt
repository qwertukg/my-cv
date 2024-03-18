package kz.qwertukg.gdp.events

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val name: String,
    val users: List<User>,
    val eventFullNames: List<String>
)

@Serializable
data class User(
    val name: String,
    val events: List<Event>
)

@Serializable
data class Event(
    val fullName: String,
    val count: Int
)