package kz.qwertukg.gdp.client

import kotlinx.serialization.Serializable

@Serializable
data class ChartJsData(
    val labels: List<String>,
    val datasets: List<ChartJsDataset>
)

@Serializable
data class ChartJsDataset(
    val label: String,
    val data: List<Int>
)
