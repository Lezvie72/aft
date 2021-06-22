package frontend.atm.administrationpanel

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
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmAdminGeneralSettingsPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("User with rights to change")
class UserWithRightsToChange : BaseTest() {

    val user1 = Users.ATM_USER_PLATFORM_ADMINISTRATOR_ROLE
    val user2 = Users.ATM_USER_FINANCE_MANAGER_ROLE
    val user3 = Users.ATM_USER_OTF_TVE_MANAGER_ROLE

    //TODO: Дописать игнор третьего шага в случае падения второго

    @ResourceLock(Constants.ATM_USER_PLATFORM_ADMINISTRATOR_ROLE)
    @TmsLink("ATMCH-4091")
    @Test
    @DisplayName("User with rights to change1")
    fun step1 () {
        with(openPage<AtmAdminGeneralSettingsPage>(driver) {submit(user1)} ) {
            step("ffffgg") {
                pageIsDisplayed()
                checkingTogglesStatusAndCorrespondingLinks("enable", "enable", "enable")
                changeToggleStatus("Streaming")
                Thread.sleep(5000)
                driver.navigate().refresh()
                Thread.sleep(5000)
                checkingTogglesStatusAndCorrespondingLinks("enable", "disable", "enable")
            }
        }

        openPage<AtmAdminGeneralSettingsPage>(driver).logout()
    }
    @ResourceLock(Constants.ATM_USER_FINANCE_MANAGER_ROLE)
    @TmsLink("ATMCH-4091")
    @Test
    @DisplayName("User with rights to change2")
    fun step2 () {
        with(openPage<AtmAdminGeneralSettingsPage>(driver) {submit(user2)} ) {
            step("fff") {
                pageIsDisplayed()
                checkingTogglesStatusAndCorrespondingLinks("enable", "disable", "enable")
                changeToggleStatus("Streaming")
                Thread.sleep(5000)
                driver.navigate().refresh()
                Thread.sleep(5000)
                checkingTogglesStatusAndCorrespondingLinks("enable", "enable", "enable")
                changeToggleStatus("RFQ", "Blocktrade")
                Thread.sleep(5000)
                driver.navigate().refresh()
                Thread.sleep(5000)
                checkingTogglesStatusAndCorrespondingLinks("disable", "enable", "disable")
            }
        }

        AtmAdminGeneralSettingsPage(driver).logout()
    }
    @ResourceLock (Constants.ATM_USER_OTF_TVE_MANAGER_ROLE)
    @TmsLink("ATMCH-4091")
    @Test
    @DisplayName("User with rights to change3")
    fun step3 () {
        with(openPage<AtmAdminGeneralSettingsPage>(driver) {submit(user1)} ) {
            pageIsDisplayed()
            checkingTogglesStatusAndCorrespondingLinks("disable", "enable", "disable")
            changeToggleStatus("RFQ", "Blocktrade")
            Thread.sleep(5000)
            driver.navigate().refresh()
            Thread.sleep(5000)
            checkingTogglesStatusAndCorrespondingLinks("enable", "enable", "enable")
        }
    }
}
