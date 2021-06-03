package pages.core.actions

import io.qameta.allure.Step
import org.hamcrest.MatcherAssert.assertThat
import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import pages.BasePage
import ru.yandex.qatools.htmlelements.element.TextBlock
import utils.helpers.attachScreenshot
import utils.helpers.to

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
    fun checkErrorAlert() {
        try {
            page.wait(4) {
                untilPresented<WebElement>(By.xpath(errorAlert.errorAlertXpath))
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
            println("Alert ${errorAlert.name} not found")
        }
    }

    @Step("Check alert")
    fun waitAndCheckErrorAlertWithMessage(waitingMessage: String) {
        try {
            val alertMessage = page.wait {
                untilPresented<WebElement>(By.xpath(errorAlert.errorAlertDescriptionXpath))
            }?.to<TextBlock>("Alert ${errorAlert.name} with message: $waitingMessage")
            attachScreenshot("Alert ${errorAlert.name} message appeared", driver)
            assertThat(
                "Body alert has $waitingMessage",
                alertMessage.text == waitingMessage
            )
        } catch (e: Error) {
            error("Alert not found")
        }
    }
}
