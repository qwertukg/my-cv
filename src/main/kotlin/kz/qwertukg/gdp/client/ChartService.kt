package kz.qwertukg.gdp.client

import kz.qwertukg.gdp.events.Project

class ChartService {
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