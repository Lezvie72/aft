package frontend.atm.otс.blocktrade

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.CC
import models.CoinType.VT
import org.apache.commons.lang.RandomStringUtils
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmP2PPage
import pages.atm.AtmWalletPage
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal


@Tags(Tag("OTC"), Tag("Blocktrade"))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("P2P Blocktrade")
@Story("Participant should be able to choose counterparty from the list while creating new offer\n")
class ParticipantShouldBeAbleToChooseCounterpartyFromListWhileCreatingNewOffer : BaseTest() {

    @TmsLink("ATMCH-2943")
    @Test
    @DisplayName("Blocktrade. Checking input methods of recipient while create offer")
    fun blockTradeCheckingInputMethodsOfRecipientWhileCreateOffer() {
        val amount = BigDecimal("3.${RandomStringUtils.randomNumeric(8)}")
        val baseAsset = CC
        val quoteAsset = VT

        val user1 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
        val user2 = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET

        val walletID =
            openPage<AtmWalletPage>(driver) { submit(user2) }.takeWalletID()
        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(user1) }) {

            e {
                click(createBlockTrade)
            }
            assert {
                elementPresented(toWallet)
            }
            e {
                sendKeys(toWallet, "123214241212")
            }
            assert {
                elementWithTextPresented("Invalid wallet address")
            }
            e {
                deleteData(toWallet)
                sendKeys(toWallet, "autotest")
                click(firstOfWalletRow)
                select(assetToSend, baseAsset.tokenSymbol)
                sendKeys(amountToSend, ".")
                sendKeys(amountToSend, amount.toString())
                select(assetToReceive, quoteAsset.tokenSymbol)
                sendKeys(amountToReceive, ".")
                sendKeys(amountToReceive, amount.toString())
                sendKeys(expiresIn, "1")
                click(createDeal)
                click(atmOtpCancel)
                deleteData(toWallet)
                sendKeys(toWallet, walletID)

            }
            assert {
                toWallet.text.equals(walletID, true)
            }

        }
    }

}


