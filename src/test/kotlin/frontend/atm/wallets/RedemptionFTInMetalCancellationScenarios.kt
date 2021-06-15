package frontend.atm.wallets

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
import pages.atm.AtmWalletPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users

@Tags(Tag(TagNames.Epic.WALLET.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Wallets")
@Story("Redemption FT in metal. Cancellation scenarios.")
class RedemptionFTInMetalCancellationScenarios : BaseTest() {

    private val user1 = Users.ATM_USER_2FA_WITH_WALLET_MTEST01

    @ResourceLock(Constants.ROLE_USER_2FA_WITH_WALLET_MTEST01)
    @TmsLink("ATMCH-2290")
    @Test
    @DisplayName("In the Amount fields, you can only enter 8 characters after the decimal point.")
    fun redemptionFTInMetalCancellationScenarios() {
        with(utils.helpers.openPage<AtmWalletPage>(driver) { submit(user1) }) {
            waitWalletsAreDisplayed()
            chooseWallet("Main 1")
            clickRedemptionButtonAndCancel()
            Thread.sleep(10000)
        }
    }
}