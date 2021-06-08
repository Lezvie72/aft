package frontend.atm.onboarding

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmProfilePage
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage

@Tags(Tag(TagNames.Epic.ONBOARDING.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Onboarding")
@Story("User Profile page structure")
class UserProfilePageStructure : BaseTest() {

    @TmsLink("ATMCH-1377")
    @Test
    @DisplayName("identification process (KYC). Сheck a new link")
    fun checkFirstLinkNotEqualToSecond() {
        val firstUrl = with(openPage<AtmProfilePage>(driver) { submit(Users.ATM_USER_KYC0) }) {
            e {
                click(startKYC)
                click(confirmStartKYC)
                wait(10L) {
                    until("KYC didn't open in 10 seconds") {
                        driver.windowHandles.size == 2
                    }
                }
                val handle = driver.windowHandles.last()
                driver.switchTo().window(handle).currentUrl
            }
        }
        driver.close()
        driver.switchTo().window(driver.windowHandles.first())
        val secondUrl = with(openPage<AtmProfilePage>(driver) { submit(Users.ATM_USER_KYC0) }) {
            e {
                click(startKYC)
                click(confirmStartKYC)
                wait(10L) {
                    until("KYC didn't open in 10 seconds") {
                        driver.windowHandles.size == 2
                    }
                }
                val handle = driver.windowHandles.last()
                driver.switchTo().window(handle).currentUrl
            }
        }
        assertTrue(firstUrl != secondUrl, "Expected $firstUrl not equal to $secondUrl")
    }

    @TmsLink("ATMCH-2041")
    @Test
    @DisplayName("Start identification process (KYC). Profile page")
    fun startIdentificationProcess() {
        with(openPage<AtmProfilePage>(driver) { submit(Users.ATM_USER_KYC0) }) {
            e {
                click(startKYC)
            }
            assert {
                elementContainingTextPresented("We will now issue a new identification link for you. Please be advised that this action will cancel all the previous identifications (if there were any) and you will be required to pass identification process from the beginning.")
                elementContainingTextPresented("In case you have already finished a videoidentification, please wait for KYC status to be changed to Reviewing. It usually takes about 10 minutes but can take up to 24h in some cases")
                elementPresented(confirmStartKYC)
                elementPresented(cancelStartKYC)
            }
        }
    }
}