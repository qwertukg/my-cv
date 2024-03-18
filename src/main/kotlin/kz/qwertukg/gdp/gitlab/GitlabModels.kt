package kz.qwertukg.gdp.gitlab

import kotlinx.serialization.Serializable

@Serializable
data class GitlabProject(
    val id: Int,
    val name: String,
)

@Serializable
data class GitlabUser(
    val id: Int,
    val username: String
)

@Serializable
data class GitlabEvent(
    val id: Int,
    val project_id: Int,
    val action_name: String,
    val target_type: String?,
    val author_username: String,
    val push_data: GitlabPushData? = null
)

@Serializable
data class GitlabPushData(
    val action: String,
    val ref_type: String,
    val ref: String?
)

