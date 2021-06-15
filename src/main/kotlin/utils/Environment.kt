package utils

import org.yaml.snakeyaml.Yaml
import utils.helpers.Users.Stand
import java.io.InputStream
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Suppress("UNCHECKED_CAST")
object Environment {

    private inline fun <reified T> Yaml.loadAs(input: InputStream): T {
        return this.loadAs(input, T::class.java)
    }

    private val properties: Properties = Properties()
    lateinit var stand: Stand

    init {
        val config = System.getProperty("config-file")
            ?: error("No config specified. try to use -Dconfig-file")
        var metaprops: Properties
        Environment::class.java.classLoader.getResourceAsStream(config).use { stream ->
            val yaml = Yaml()
            fun prefixKeys(key: String, value: Map<*, *>): Map<String, *> {
                return value.mapKeys { key + "." + it.key.toString() }
            }
            metaprops = yaml.loadAs(stream!!)
            metaprops.forEach { key, value ->
                fun mapProps(key: String, value: Any) {
                    when (value) {
                        is Map<*, *> -> prefixKeys(key, value).forEach { mapProps(it.key, it.value!!) }
                        else -> properties[key] = value.toString()
                    }
                }
                mapProps(key.toString(), value)
            }

            val standProp =
                getProperty("stand").takeUnless { it.isEmpty() } ?: error("Undefined config property: stand")
            stand = when (standProp) {
                "develop" -> Stand.DEVELOP
                "release" -> Stand.RELEASE
                "preprod" -> Stand.PREPROD
                "prod" -> Stand.PROD
                "shared" -> Stand.SHARED
                "tokentrust" -> Stand.TOKEN_TRUST
                else -> error("Unknown stand $standProp")
            }
        }
    }

    fun getProperty(propertyName: String, defaultValue: String? = null): String {
        return properties.getProperty(propertyName) ?: defaultValue ?: ""
    }

    val jiraRestUsername =
        System.getenv("K8S_SECRET_JIRA_REST_USERNAME") ?: getProperty("services.jira.rest.username", "")
    val jiraRestApiToken =
        System.getenv("K8S_SECRET_JIRA_REST_TOKEN") ?: getProperty("services.jira.rest.token", "")
    val jiraCloudAccessKey =
        System.getenv("K8S_SECRET_JIRA_CLOUD_ACCESS_KEY")
            ?: getProperty("services.jira.cloud.accessKey", "")
    val jiraCloudSecretKey =
        System.getenv("K8S_SECRET_JIRA_CLOUD_SECRET_KEY")
            ?: getProperty("services.jira.cloud.secretKey", "")
    val jiraCloudProjectId =
        System.getenv("K8S_SECRET_JIRA_CLOUD_PROJECT_ID")
            ?: getProperty("services.jira.cloud.projectId", "")
    val jiraCloudUserId =
        System.getenv("K8S_SECRET_JIRA_CLOUD_USER_ID") ?: getProperty("services.jira.cloud.userId", "")

    val gitlabSecretKey =
        System.getenv("K8S_SECRET_GIT_SECRET_KEY") ?: getProperty("services.gitlab.secretKey", "")

    val gitlabJobId = System.getenv("CI_JOB_ID") ?: "local run - ${LocalDateTime.now(ZoneId.of("UTC"))
        .format(DateTimeFormatter.ISO_DATE)}"
    val gitlabProjectNamespace = System.getenv("CI_PROJECT_NAMESPACE") ?: ""
    val gitlabProjectName = System.getenv("CI_PROJECT_NAME") ?: ""
    val gitlabPagesUrl = System.getenv("CI_PAGES_DOMAIN") ?: ""

    val targetGitlabProject = System.getProperty("gitProject") ?: "WEBFRONT"
    val targetGitlabBranch = System.getProperty("gitBranch") ?: "release/v1.3.0"
    val targetJiraReleases = System.getProperty("releases")?.takeUnless { it.isEmpty() }

    val atm_front_login = getProperty("front.atm.login")
    val atm_front_password = getProperty("front.atm.password")
    val atm_front_base_url = getProperty("front.atm.url", "")
    val atm_front_url = "https://$atm_front_login:$atm_front_password@$atm_front_base_url"

    val organizationForRegistration = getProperty("orgForRegistration", "autotesst")
}