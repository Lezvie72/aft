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
import pages.atm.AtmWalletPage
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage

@Tags(Tag(TagNames.Epic.ACCOUNTSETTINGS.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Account settings")
@Story("Removing the Refresh button for Reference number")
class RemovingTheRefreshButtonForReferenceNumber : BaseTest() {

    @TmsLink("ATMCH-6335")
    @Test
    @DisplayName("Checking for changes in FIAT DEPOSIT DETAILS")
    fun checkingForChangesInFiatDepositDetails() {
        val user = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF
        val wallet = user.mainWallet

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            chooseWallet(wallet.name)
            e {
                click(depositDetails)
            }
            assert {
                elementContainingTextPresented("Reference ID")
                elementNotPresented(refreshReference)
                elementNotPresented(referenceNumberHistory)
            }
        }
    }

}