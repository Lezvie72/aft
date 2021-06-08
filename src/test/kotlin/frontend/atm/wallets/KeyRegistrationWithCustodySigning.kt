package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
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
@Story("Movement from main to OTF and from OTF to main wallets")
class KeyRegistrationWithCustodySigning : BaseTest() {
    //preconditions
    private val user1 = Users.ATM_USER_2FA_WITH_WALLET_MTEST01
    private val user2 = Users.ATM_USER_2FA_WITHOUT_WALLET_MTEST02


    @ResourceLock(Constants.ROLE_USER_2FA_WITH_WALLET_MTEST01)
    @TmsLink("ATMCH-4007")
    @Test
    @DisplayName("Wallet/key registration.First wallet with custody signing. ")
    fun checkCreatingFirstCustodyWallet() {

        val walletName = "Main ${RandomStringUtils.randomNumeric(3)}"

        with(utils.helpers.openPage<AtmWalletPage>(driver) { submit(user2) }) {
            checkRegisterFirstCustodyWallet(walletName)
        }
    }

    @ResourceLock(Constants.ROLE_USER_2FA_WITH_WALLET_MTEST01)
    @TmsLink("ATMCH-4023")
    @Test
    @DisplayName("Wallet /key registration. Custody signing. Creating one more wallet")
    fun keyRegistrationCustodySigningCreatingOneMoreWallet() {

        val walletName = "Main ${RandomStringUtils.randomNumeric(3)}"

        with(utils.helpers.openPage<AtmWalletPage>(driver) { submit(user1) }) {
            registerWalletWithCustodianSignin(user1.castodian, walletName)
            e {
                assertThat(
                    "Wallet with label $walletName wasn't found",
                    isWalletWithLabelPresented(walletName)
                )
            }
        }
    }
}