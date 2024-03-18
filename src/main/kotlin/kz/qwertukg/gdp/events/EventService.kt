package kz.qwertukg.gdp.events

import kz.qwertukg.gdp.EventRequestBody
import kz.qwertukg.gdp.gitlab.*
import java.time.LocalDate

class EventService(private val gitlabService: GitlabService) {
    suspend fun getProjectEventsByUserAndEvents(body: EventRequestBody): Project {
        val gitlabProject = gitlabService.getOne<GitlabProject>("projects/${body.projectId}")
        val gitlabProjectEvents = gitlabService.getAll<GitlabEvent>("projects/${body.projectId}/events", LocalDate.parse(body.after))

        val gitlabProjectUsers = getProjectUsersOnly(body)
        val gitlabProjectEventsKeys = getSortedEventFullNames(gitlabProjectEvents, body)

        val projectUsers = gitlabProjectUsers.mapNotNull { gitlabUser ->
            createUserFromEvents(gitlabUser, gitlabProjectEvents, gitlabProjectEventsKeys).takeIf { user ->
                user.events.any { it.count > 0 }
            }
        }.sortedByDescending { user ->
            user.events.sumOf { it.count }
        }

        return Project(
            name = gitlabProject.name,
            users = projectUsers,
            eventFullNames = gitlabProjectEventsKeys,
        )
    }

    private fun getSortedEventFullNames(events: List<GitlabEvent>, body: EventRequestBody): List<String> {
        val keys = events.groupBy(GitlabEvent::getFullName).keys
        return if (body.possibleEvents.isNotEmpty()) keys.filter { it in body.possibleEvents }.sorted()
        else keys.sorted()
    }

    private suspend fun getProjectUsersOnly(body: EventRequestBody): List<GitlabUser> {
        val users = gitlabService.getAll<GitlabUser>("projects/${body.projectId}/users", LocalDate.parse(body.after))
        return if (body.possibleUsers.isNotEmpty()) users.filter { it.username in body.possibleUsers }
        else users
    }

    private fun createUserFromEvents(
        gitlabUser: GitlabUser,
        gitlabProjectEvents: List<GitlabEvent>,
        eventFullNames: List<String>
    ): User {
        val eventsOfUser = gitlabProjectEvents.filter { it.author_username == gitlabUser.username }
        val eventsGroupedByName = eventsOfUser.groupBy(GitlabEvent::getFullName)

        val userEvents = eventFullNames.map { name ->
            Event(
                fullName = name,
                count = eventsGroupedByName[name]?.size ?: 0
            )
        }

        return User(
            name = gitlabUser.username,
            events = userEvents
        )
    }

    fun getChartJsData(project: Project): ChartJsData {
        return ChartJsData(
            labels = project.eventFullNames,
            datasets = project.users.map { user ->
                ChartJsDataset(
                    label = user.name,
                    data = user.events.map { it.count }
                )
            }
        )
    }

}

