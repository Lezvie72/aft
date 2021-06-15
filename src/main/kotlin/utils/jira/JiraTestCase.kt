package utils.jira

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class JiraTestCase(val jiraTask: String)