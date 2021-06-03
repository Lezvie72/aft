package extensions

import io.qameta.allure.TmsLink
import org.junit.jupiter.api.extension.*
import utils.jira.JiraZapi
import utils.jira.TestStatus
import java.lang.reflect.Method

class JiraExtension : BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback {

    companion object {
        private var cycleId: String = ""
        private val cache = mutableMapOf<String, String>()
    }


    /**
     * Since this method should be executed only once per run, variable [cycleId] shows, was this method executed already
     */
    override fun beforeAll(context: ExtensionContext?) {
        synchronized(cycleId) {
            if (cycleId.isEmpty()) {
                if (JiraZapi.isInitialized()) {
                    cycleId = JiraZapi.createTestCycle()
                }
            }
        }
    }

    override fun beforeEach(context: ExtensionContext?) {
        if (JiraZapi.isInitialized()) {
            val method = context?.testMethod?.orElseGet(null)
            val jiraTaskId = method?.let {
                if (it.isAnnotationPresent(TmsLink::class.java)) it.getAnnotation(TmsLink::class.java).value else null
            }
            jiraTaskId?.let {
                val executionId = JiraZapi.createExecution(it, cycleId)
                cache.put(context.uniqueId, executionId)
            }
        }
    }

    override fun afterEach(context: ExtensionContext?) {
        context?.let {
            if (JiraZapi.isInitialized()) {
                val testMethod = context.testMethod?.orElseGet(null)
                val jiraTaskId = getJiraTaskId(testMethod)
                val testStatus =
                    if (context.executionException.isPresent) TestStatus.FAIL else TestStatus.PASS
                updateExecutionStatus(context.uniqueId, jiraTaskId, testStatus)
            }
        }
    }

    private fun getJiraTaskId(method: Method?): String {
        return method?.let {
            if (it.isAnnotationPresent(TmsLink::class.java)) {
                it.getAnnotation(TmsLink::class.java).value
            } else {
                ""
            }
        } ?: ""
    }

    private fun updateExecutionStatus(testId: String, jiraTaskId: String, testStatus: TestStatus) {
        val executionId = cache[testId] ?: ""
        if (executionId.isNotEmpty() && jiraTaskId.isNotEmpty() && cycleId.isNotEmpty()) {
            JiraZapi.updateExecution(executionId, jiraTaskId, cycleId, testStatus)
        }
    }

    override fun afterAll(context: ExtensionContext?) {
    }
}