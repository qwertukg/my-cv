package kz.qwertukg.gdp.events

import io.ktor.util.*
import kz.qwertukg.gdp.gitlab.GitlabEvent

fun GitlabEvent.getBranchType(): BranchType? {
    val branchName = push_data?.ref?.toLowerCasePreservingASCIIRules() ?: return null
    return BranchType.entries.find { branchType -> branchType.possibleNames.any { it in branchName } } ?: BranchType.Other
}

fun GitlabEvent.getFullName(): String {
    val branchType = getBranchType()

    val fullName = if (branchType != null) {
        "$action_name $branchType"
    } else if (target_type != null) {
        "$action_name $target_type"
    } else {
        action_name
    }

    return fullName.toLowerCasePreservingASCIIRules().capitalize()
}

enum class BranchType(val possibleNames: Set<String>) {
    Feature(setOf("feature")),
    Hotfix(setOf("hotfix")),
    Bugfix(setOf("bugfix")),
    Release(setOf("release")),
    Dev(setOf("dev", "development")),
    Test(setOf("test", "stage", "staging")),
    Prod(setOf("prod", "production", "main", "master")),
    Other(setOf())
}

