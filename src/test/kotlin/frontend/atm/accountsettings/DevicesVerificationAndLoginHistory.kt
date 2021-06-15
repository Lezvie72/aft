package frontend.atm.accountsettings

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.openqa.selenium.By
import pages.atm.*
import ru.yandex.qatools.htmlelements.element.Button
import utils.Environment
import utils.TagNames
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.authorize
import utils.helpers.openPage
import utils.helpers.step

@Tags(Tag(TagNames.Epic.ACCOUNTSETTINGS.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Account settings")
@Story("Devices Verification And Login History")
class DevicesVerificationAndLoginHistory : BaseTest() {

    @TmsLink("ATMCH-135")
    @Test
    @DisplayName("Verification of a new device")
    fun verificationOfNewDevice() {
        val user = newUser()

        step("GIVEN user opens site from unverified device") {
            with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
                sendInvitation(user.email, true)
            }
            openPage<AtmHomePage>(driver)
            val href = GmailApi.getHrefForNewUserATM(user.email)
            driver.navigate().to(href)
            with(AtmLoginPage(driver)) {
                fillRegForm()
            }
            with(openPage<AtmProfilePage>(driver) { submit(user) }) {
                e {
                    click(devices)
                }
            }
            with(AtmDevicesPage(driver)) {
                e {
                    deleteCurrentDevice()
                }
            }
        }

        with(AtmLoginPage(driver)) {
            e {
                sendKeys(atmUserEmail, user.email)
                sendKeys(atmUserPassword, user.password)
                click(signInButton)
            }
            assert { elementPresented(verificationNeeded) }
            verifyDeviceIfNeeded(user)
            driver.authorize(Environment.atm_front_base_url)
        }

        with(AtmProfilePage(driver)) {
            e {
                click(devices)
            }
            assert { urlEndsWith("/profile/devices") }
        }
        with(openPage<AtmDevicesPage>(driver)) {
            e {
                isCurrentDeviceVerified()
            }
        }
        with(AtmProfilePage(driver).logout()) {
            e {
                sendKeys(atmUserEmail, user.email)
                sendKeys(atmUserPassword, user.password)
                click(signInButton)
            }
            assert { urlEndsWith("/profile/info") }
        }
    }

    @TmsLink("ATMCH-152")
    @Test
    @DisplayName("Removing verified device")
    fun removingVerifiedDevice() {
        val user = newUser()
        with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            sendInvitation(user.email, true)
        }
        openPage<AtmHomePage>(driver)
        val href = GmailApi.getHrefForNewUserATM(user.email)
        driver.navigate().to(href)
        with(AtmLoginPage(driver)) {
            fillRegForm()
        }
        with(AtmProfilePage(driver)) {
            e {
                click(devices)
            }
            assert { urlEndsWith("/profile/devices") }
        }
        with(openPage<AtmDevicesPage>(driver)) {
            val (currentDeviceId, crntDeviceBlock) =
                e {
                    val currentDeviceId = currentDevice.text.take(36)
                    val crntDeviceBlock = wait {
                        untilPresented<Button>(By.xpath("//h2[contains(text(), 'Verified devices')]/following-sibling::atm-device/div[contains(text(),'$currentDeviceId')]"))
                    }
                    e {
                        click(crntDeviceBlock)
                    }
                    currentDeviceId to crntDeviceBlock
                }
            e {
                click(crntDeviceBlock)
                val deleteDeviceButton =
                    wait {
                        untilPresented<Button>(By.xpath("//h2[contains(text(), 'Verified devices')]/following-sibling::atm-device/div[contains(text(),'$currentDeviceId')]/ancestor::atm-device//button"))
                    }
                click(deleteDeviceButton)
            }
            assert {
                elementPresented(confirmDeleteDevice)
                elementPresented(cancelDeleteDevice)
            }
            e {
                click(cancelDeleteDevice)
            }
            assert { isCurrentDeviceVerified() }
            e {
                click(crntDeviceBlock)
                val deleteDeviceButton =
                    wait {
                        untilPresented<Button>(By.xpath("//h2[contains(text(), 'Verified devices')]/following-sibling::atm-device/div[contains(text(),'$currentDeviceId')]/ancestor::atm-device//button"))
                    }
                click(deleteDeviceButton)
                click(confirmDeleteDevice)
            }
            assert { urlEndsWith("/login") }

            val textMessage = GmailApi.getTextThatDeviceRemoved(user.email)
            assert { textMessage.contains("Your device ($currentDeviceId) has been removed") }
        }
    }

    @TmsLink("ATMCH-153")
    @Test
    @DisplayName("Device Control and Authorization Log")
    fun deviceControlAndAuthorizationLog() {
        with(openPage<AtmProfilePage>(driver) { submit(Users.ATM_USER_KYC0_2FA_NONE) }) {
            e {
                click(devices)
            }
            assert { urlEndsWith("/profile/devices") }
        }
        with(openPage<AtmDevicesPage>(driver)) {
            assert {
                elementPresented(currentDeviceSection)
                elementPresented(verifiedDeviceSection)
                elementPresented(loginHistoryDeviceID)
                elementPresented(loginHistoryDeviceIP)
                elementPresented(loginHistoryDeviceLocation)
                elementPresented(loginHistoryDeviceLastLoginDate)
                elementPresented(loginHistoryLastLoginTime)
            }
            assert { isDataOfCurrentLoginDisplayedInLoginHistoryPart() }
        }
    }

}