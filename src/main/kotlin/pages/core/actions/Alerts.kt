package pages.core.actions

import io.qameta.allure.Step
import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriver
import pages.BasePage
import ru.yandex.qatools.htmlelements.element.TextBlock
import utils.helpers.attachScreenshot

class Alerts<T : WebDriver>(page: BasePage, driver: T) : BaseActions<BasePage, T>(page, driver) {
    data class Alert(
        val name: String,
        val errorAlertXpath: String,
        val errorAlertDescriptionXpath: String
    )

    companion object {
        private val errorAlert = Alert(
            "Error",
            "//*[contains(text(), 'Error')]",
            "//div[contains(@class, 'ant-notification-notice-description')]"
        )
    }

    @Step("Check error alert")
    fun checkErrorAlert(timeout: Long = 4) {
        try {
            page.wait(timeout) {
                untilPresented<TextBlock>(By.xpath(errorAlert.errorAlertXpath))
            }
            try {
                attachScreenshot("Alert ${errorAlert.name} message appeared", driver)
                error(
                    "The operation could not be completed - an error window appeared: ${
                        page.findElement(
                            By.xpath(errorAlert.errorAlertDescriptionXpath)
                        ).text
                    }"
                )
            } catch (e: Error) {
                error("The operation could not be completed - an error window appeared without a description")
            }
        } catch (e: TimeoutException) {
        }
    }

    @Step("Check alert")
    fun waitAndCheckErrorAlertWithMessage(waitingMessage: String) {
        page.wait {
            until("Message with text: $waitingMessage - should be appeared", 10L) {
                page.check {
                    isElementWithTextPresented(waitingMessage, 1L)
                }
            }
        }
    }
}
