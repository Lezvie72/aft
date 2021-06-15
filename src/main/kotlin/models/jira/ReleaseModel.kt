package models.jira

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ReleaseModel(
    @JsonProperty("archived")
    val archived: Boolean = false,
    @JsonProperty("overdue")
    val overdue: Boolean = false,
    @JsonProperty("releaseDate")
    val releaseDate: String = "",
    @JsonProperty("name")
    val name: String = "",
    @JsonProperty("self")
    val self: String = "",
    @JsonProperty("userReleaseDate")
    val userReleaseDate: String = "",
    @JsonProperty("id")
    val id: String = "",
    @JsonProperty("userStartDate")
    val userStartDate: String = "",
    @JsonProperty("projectId")
    val projectId: Int = 0,
    @JsonProperty("released")
    val released: Boolean = false,
    @JsonProperty("startDate")
    val startDate: String = "",
    @JsonProperty("description")
    val description: String = ""
)