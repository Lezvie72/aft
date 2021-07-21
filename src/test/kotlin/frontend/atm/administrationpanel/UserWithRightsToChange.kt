package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.junit.Assume.assumeTrue
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


var switch: Boolean? = null


@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.SAME_THREAD)
@Epic("Frontend")
@Feature("Administration panel")
@Story("User with rights to change")
class UserWithRightsToChange : BaseTest() {

    val user1 = Users.ATM_USER_PLATFORM_ADMINISTRATOR_ROLE
    val user2 = Users.ATM_USER_FINANCE_MANAGER_ROLE
    val user3 = Users.ATM_USER_OTF_TVE_MANAGER_ROLE

    @ResourceLock(Constants.ATM_USER_PLATFORM_ADMINISTRATOR_ROLE)
    @TmsLink("ATMCH-4091")
    @Test
    @DisplayName("User with rights to change step 1")
    fun userWithRightsToChangeStep1 () {
        switch = false
        with(openPage<AtmAdminGeneralSettingsPage>(driver) {submit(user1)} ) {
            pageIsDisplayed()
            checkingTogglesStatusAndSwitchingToCorrect()
            checkingTogglesStatusAndCorrespondingLinks("enable", "enable", "enable")
            changeToggleStatus("Streaming")
            driver.navigate().refresh()
            checkingTogglesStatusAndCorrespondingLinks("enable", "disable", "enable")
        }
        openPage<AtmAdminGeneralSettingsPage>(driver).logout()
        switch = true
    }

    @ResourceLock(Constants.ATM_USER_FINANCE_MANAGER_ROLE)
    @TmsLink("ATMCH-4091")
    @Test
    @DisplayName("User with rights to change step 2")
    fun userWithRightsToChangeStep2 () {
        assumeTrue(switch!!)
        switch = false
        with(openPage<AtmAdminGeneralSettingsPage>(driver) {submit(user2)} ) {
            pageIsDisplayed()
            checkingTogglesStatusAndCorrespondingLinks("enable", "disable", "enable")
            changeToggleStatus("Streaming")
            driver.navigate().refresh()
            checkingTogglesStatusAndCorrespondingLinks("enable", "enable", "enable")
            changeToggleStatus("RFQ", "Blocktrade")
            driver.navigate().refresh()
            checkingTogglesStatusAndCorrespondingLinks("disable", "enable", "disable")
        }
        AtmAdminGeneralSettingsPage(driver).logout()
        switch = true
    }

    @ResourceLock (Constants.ATM_USER_OTF_TVE_MANAGER_ROLE)
    @TmsLink("ATMCH-4091")
    @Test
    @DisplayName("User with rights to change step 3")
    fun userWithRightsToChangeStep3 () {
        assumeTrue(switch!!)
        with(openPage<AtmAdminGeneralSettingsPage>(driver) {submit(user3)} ) {
            pageIsDisplayed()
            checkingTogglesStatusAndCorrespondingLinks("disable", "enable", "disable")
            changeToggleStatus("RFQ", "Blocktrade")
            driver.navigate().refresh()
            checkingTogglesStatusAndCorrespondingLinks("enable", "enable", "enable")
        }
    }

}