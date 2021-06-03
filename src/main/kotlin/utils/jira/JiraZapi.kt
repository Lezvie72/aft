package utils.jira

import com.thed.zephyr.cloud.rest.ZFJCloudRestClient
import com.thed.zephyr.cloud.rest.client.JwtGenerator
import io.restassured.RestAssured
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import models.jira.ReleaseModel
import org.junit.jupiter.api.Test
import utils.Environment
import utils.Environment.gitlabJobId
import utils.Environment.gitlabPagesUrl
import utils.Environment.gitlabProjectName
import utils.Environment.gitlabProjectNamespace
import utils.helpers.toList
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


object JiraZapi {

    enum class Method(val value: String) {
        GET("GET"), POST("POST"), DELETE("DELETE"), PUT("PUT");

        override fun toString(): String {
            return value
        }
    }

    private val versionId = -1
    private val accessKey = Environment.jiraCloudAccessKey
    private val secretKey = Environment.jiraCloudSecretKey
    private val userId = Environment.jiraCloudUserId
    private val projectId = Environment.jiraCloudProjectId

    private val restUsername = Environment.jiraRestUsername
    private val restToken = Environment.jiraRestApiToken

    val jobId: String = try {
        (gitlabJobId.toLong() + 1).toString()
    } catch (e: NumberFormatException) {
        ""
    }

    private val reportUrl = if (jobId.isNotEmpty()) {
        "http://$gitlabProjectNamespace.$gitlabPagesUrl/-/$gitlabProjectName/-/jobs/$jobId/artifacts/public/index.html"
    } else {
        "Report isn't enabled for local run"
    }

    fun isInitialized(): Boolean {
        return accessKey.isNotEmpty()
                && secretKey.isNotEmpty()
                && userId.isNotEmpty()
                && projectId.isNotEmpty()
                && restUsername.isNotEmpty()
                && restToken.isNotEmpty()
    }

    val baseZapiUrl = "https://prod-api.zephyr4jiracloud.com/connect"

    private val jwt by lazy {
        getJwtGenerator()
    }

    @Test
    fun t() {
        val id = "24c4e6f3-f4bf-430e-9668-336aa4ca8f2a"
        val endpoint = "/public/rest/api/1.0/cycle/$id?versionId=-1&projectId=10047"
        val token = createTokenForURI(Method.GET, endpoint)
        val response = cloudSpec(token)
            .contentType(ContentType.TEXT)
            .filter(RequestLoggingFilter())
            .filter(ResponseLoggingFilter())
            .get(endpoint)
        println(response)
    }

    private fun getJwtGenerator(): JwtGenerator {
        val client = ZFJCloudRestClient.restBuilder(
            baseZapiUrl,
            accessKey,
            secretKey,
            userId
        ).build()
        return client.jwtGenerator
    }

    private fun createTokenForURI(method: Method, url: String): String {
        val uri = URI("$baseZapiUrl$url")
        return jwt.generateJWT(method.value, uri, 3600)
    }

    private fun cloudSpec(token: String): RequestSpecification {
        return RestAssured.given()
            .baseUri(baseZapiUrl)
            .headers("Authorization", token, "zapiAccessKey", accessKey)
            .contentType(ContentType.JSON)
    }

    private fun restSpec(): RequestSpecification {
        return RestAssured.given()
            .baseUri("https://sdexnt.atlassian.net")
            .auth().preemptive().basic(restUsername, restToken)
            .contentType(ContentType.JSON)
    }

    fun createTestCycle(): String {
        val endpoint = "/public/rest/api/1.0/cycle"
        val token = createTokenForURI(
            Method.POST,
            endpoint
        )
        val body = object {
            val utc = ZoneId.of("UTC")
            val name = LocalDateTime.now(utc).toString()
            val build = "1.0"
            val environment = Environment.atm_front_base_url
            val description = "Auto-test run at $name. \n" +
                    "Job id: $gitlabJobId\n" +
                    "Report is available at: \n" +
                    reportUrl
            val startDate = LocalDate.now(utc).format(DateTimeFormatter.ISO_DATE)
            val endDate = LocalDate.now(utc).format(DateTimeFormatter.ISO_DATE)
            val versionId = JiraZapi.versionId
            val clearCustomFieldsFlag = true
            val projectId = JiraZapi.projectId
        }

        val response = cloudSpec(token)
            .body(body)
            .post(endpoint).thenReturn().jsonPath()

        return response.getString("id")
    }

    fun createExecution(testId: String, cycleId: String): String {
        val jiraTaskId = restSpec()
            .pathParam("taskId", testId)
            .get("/rest/api/latest/issue/{taskId}")
            .thenReturn().jsonPath().getString("id")

        val endpoint = "/public/rest/api/1.0/execution"
        val token = createTokenForURI(Method.POST, endpoint)


        val body = object {
            val projectId = JiraZapi.projectId
            val issueId = jiraTaskId
            val cycleId = cycleId
            val versionId = JiraZapi.versionId
        }

        return cloudSpec(token)
            .body(body)
            .post(endpoint)
            .thenReturn().jsonPath().getString("execution.id")
    }

    fun updateExecution(executionId: String, testId: String, cycleId: String, status: TestStatus) {
        val jiraTaskId = restSpec()
            .pathParam("taskId", testId)
            .get("/rest/api/latest/issue/{taskId}")
            .thenReturn().jsonPath().getString("id")

        val endpoint = "/public/rest/api/1.0/execution/$executionId"
        val token = createTokenForURI(Method.PUT, endpoint)

        val body = object {
            val status = object {
                val id = status.value
            }
            val projectId = JiraZapi.projectId
            val issueId = jiraTaskId
            val cycleId = cycleId
            val versionId = JiraZapi.versionId
        }

        cloudSpec(token)
            .body(body)
            .put(endpoint)

//        createComment(
//            testId, "Test was executed by '$gitlabJobId' with '$status'.\n" +
//                    "Report is available at:\n" +
//                    reportUrl
//        )
    }

    private fun createComment(taskId: String, content: String) {
        restSpec()
            .pathParam("taskId", taskId)
            .body(object {
                val body = content
            })
            .post("/rest/api/2/issue/{taskId}/comment")
    }

    fun getReleasesByIds(
        project: String,
        vararg ids: String
    ): List<ReleaseModel> {
        val releases = restSpec()
            .pathParam("project", project)
            .get("/rest/api/3/project/{project}/version").toList<ReleaseModel>("values")
        return releases.filter {
            it.id in ids
        }

    }

    fun getReleasedReleasesByMask(
        project: String
    ): List<String> {
        val releases = restSpec()
            .pathParam("project", project)
            .queryParam("status", "released")
            .get("/rest/api/3/project/{project}/version").toList<ReleaseModel>("values")

        return releases.sortedByDescending {
            it.startDate
        }.map {
            it.id
        }
    }

    fun getTaskNameListByJql(jql: String): List<String> {
        val result = mutableListOf<String>()

        val maxResults = 100
        var startAt = 0

        do {
            val response = restSpec()
                .queryParam("jql", jql)
                .queryParam("maxResults", maxResults)
                .queryParam("startAt", startAt)
                .get("/rest/api/3/search")
            val total = response.thenReturn().body.jsonPath().getInt("total")
            startAt += maxResults
            result.addAll(response.thenReturn().body.jsonPath().getList("issues.key"))
        } while (startAt <= total)

        return result
    }

}

enum class TestStatus(val value: Int) {
    UNEXECUTED(-1), PASS(1), FAIL(2), WIP(3), BLOCKED(4)
}