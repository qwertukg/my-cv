package kz.qwertukg.gdp.events

import kz.qwertukg.gdp.gitlab.*
import java.time.LocalDate

class EventService(private val gitlabService: GitlabService) {

    suspend fun getProjectEventsByUser(projectId: Int, after: LocalDate): Project {
        val gitlabProject = gitlabService.getOne<GitlabProject>("projects/$projectId")
        val gitlabProjectUsers = gitlabService.getAll<GitlabUser>("projects/$projectId/users", after)
        val gitlabProjectEvents = gitlabService.getAll<GitlabEvent>("projects/$projectId/events", after)

        // Извлечение генерации отсортированных полных имен событий в функцию для ясности.
        val gitlabProjectEventsKeys = getSortedEventFullNames(gitlabProjectEvents)

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

    // Извлечение сортировки полных имен событий в отдельную функцию для ясности.
    private fun getSortedEventFullNames(events: List<GitlabEvent>): List<String> =
        events.groupBy(GitlabEvent::getFullName).keys.sorted()

    // Абстракция создания объекта User из GitlabUser и событий, с которыми они связаны.
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

