package pages.atm

import io.qameta.allure.Step
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.core.annotations.PageName
import pages.core.annotations.PageUrl
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.Image
import ru.yandex.qatools.htmlelements.element.TextBlock
import utils.Environment
import utils.helpers.logout

@PageUrl("/profile/devices")
@PageName("Devices page")
class AtmDevicesPage(driver: WebDriver) : AtmPage(driver) {


    @FindBy(css = "atm-device.device--current > div:first-child")
    @Name("Current device")
    lateinit var currentDevice: TextBlock

    @FindBy(xpath = "//button//span[contains(text(),'Delete')]")
    @Name("Confirm delete device")
    lateinit var confirmDeleteDevice: Button

    @FindBy(xpath = "//span[contains(text(),'Cancel')]")
    @Name("Cancel delete device")
    lateinit var cancelDeleteDevice: Button

    @FindBy(xpath = "//h2[contains(text(),'Current device')]")
    @Name("Current device section")
    lateinit var currentDeviceSection: TextBlock

    @FindBy(xpath = "//h2[contains(text(),'Verified devices')]")
    @Name("Verified device section")
    lateinit var verifiedDeviceSection: TextBlock

    @Name("Login history device id: identifier of device")
    @FindBy(xpath = "//span[@class='login-history__span-browser']")
    lateinit var loginHistoryDeviceID: TextBlock

    @Name("Login history IP address")
    @FindBy(xpath = "//span[@class='login-history__span-ip']")
    lateinit var loginHistoryDeviceIP: TextBlock

    @Name("Login history location")
    @FindBy(xpath = "//td[contains(@class,'login-history__location')]")
    lateinit var loginHistoryDeviceLocation: TextBlock

    @Name("Login history last login date")
    @FindBy(xpath = "//span[@class='login-history__span-date']")
    lateinit var loginHistoryDeviceLastLoginDate: TextBlock

    @Name("Login history time of the last login")
    @FindBy(xpath = "//span[@class='login-history__span-time']")
    lateinit var loginHistoryLastLoginTime: TextBlock

    @Name("Login history 2FA")
    @FindBy(xpath = "//i[@class='fas fa-check-circle']")
    lateinit var loginHistory2FA: Image

    @Step("User deletes current device")
    fun deleteCurrentDevice() {
        val currentDeviceId = wait {
            untilPresented(currentDevice)
        }.text.take(36)
        e {
            click(wait {
                untilPresented<Button>(By.xpath("//h2[contains(text(), 'Verified devices')]/following-sibling::atm-device/div[contains(text(),'$currentDeviceId')]"))
            })
            val deleteDeviceButton = wait {
                untilPresented<Button>(By.xpath("//h2[contains(text(), 'Verified devices')]/following-sibling::atm-device/div[contains(text(),'$currentDeviceId')]/ancestor::atm-device//button"))
            }
            click(deleteDeviceButton)
            click(confirmDeleteDevice)
            driver.logout(Environment.atm_front_base_url)
        }
    }

    @Step("Check: is current device verified")
    fun isCurrentDeviceVerified(): Boolean {
        val currentDeviceId = currentDevice.text.take(36)
        return check {
            isElementPresented(By.xpath("//atm-device[contains(@class,'device--verified')]//div[contains(@class,'device__title')][contains(text(),'$currentDeviceId')]"))
        }
    }

    @Step("Check: is current device presented in login history")
    fun isDataOfCurrentLoginDisplayedInLoginHistoryPart(): Boolean {
        val currentDeviceId = currentDevice.text.take(36)
        val currentDeviceLabel =
            findElement(By.xpath("//atm-login-history//span[contains(text(),'$currentDeviceId')]"))
        return currentDeviceLabel.isDisplayed
    }


}