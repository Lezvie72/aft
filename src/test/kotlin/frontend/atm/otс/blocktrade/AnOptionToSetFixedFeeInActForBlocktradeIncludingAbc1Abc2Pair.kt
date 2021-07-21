package frontend.atm.otс.blocktrade

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import models.user.classes.DefaultUser
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.*
import pages.atm.AtmAdminStreamingSettingsPage.FeeModeState
import pages.atm.AtmAdminStreamingSettingsPage.FeeModeState.*
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal

@Tags(Tag(TagNames.Flow.OTC), Tag(TagNames.Epic.BLOCKTRADE.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("P2P Blocktrade")
@Story("Acceptance of an offer and performance of a transaction")
@TmsLink("ATMCH-2496")
class AnOptionToSetFixedFeeInActForBlocktradeIncludingAbc1Abc2Pair : BaseTest() {
    private val userOne = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val userTwo = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04
    private val firstToken = CoinType.CC
    private val secondToken = CoinType.VT

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-4004")
    @Test
    @DisplayName("Accept incoming blocktrade. An option to set fixed fee.")
    fun acceptIncomingBlocktradeAnOptionToSetFixedFee() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        val feeSizeForMaker = "1"
        val feeSizeForTaker = "2"

        step("Precondition - set fee") {
            with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                changeFeeSettingsForTokenBlocktrade(
                    firstToken,
                    FIXED.state,
                    feeSizeForMaker,
                    FIXED.state,
                    feeSizeForTaker,
                    firstToken
                )
            }
            with(openPage<AtmAdminBlocktradeSettingsPage>(driver)) {
                changeFeeSettingsForTokenBlocktrade(
                    secondToken,
                    FIXED.state,
                    feeSizeForMaker,
                    FIXED.state,
                    feeSizeForTaker,
                    firstToken
                )
            }
        }

        step("Precondition - create offer") {
            val (walletID, companyName) = step("GIVEN user1 walletID is known") {
                val companyName = openPage<AtmProfilePage>(driver) { submit(userTwo) }.getCompanyName()
                val walletID = openPage<AtmWalletPage>(driver).takeWalletID()

                walletID to companyName
            }
            openPage<AtmWalletPage>(driver).logout()

            with(openPage<AtmP2PPage>(driver) { submit(userOne) }) {
                e {
                    createP2P(
                        walletID,
                        companyName,
                        firstToken,
                        amount.toString(),
                        secondToken,
                        amount.toString(),
                        AtmP2PPage.ExpireType.TEMPORARY,
                        userOne
                    )

                    MatcherAssert.assertThat(
                        "Offer with ${amount.toDouble()} should be visible after created",
                        isOfferExist(amount, outgoingOffers)
                    )
                }
            }
            openPage<AtmWalletPage>(driver).logout()
        }

        with(openPage<AtmP2PPage>(driver) { submit(userTwo) }) {
            e {
                findIncomingP2P(amount)
                wait(15L) {
                    until("Couldn't load fee") {
                        offerFee.text.isNotEmpty()
                    }
                    offerFee.amount
                }

                MatcherAssert.assertThat(
                    "AMOUNT TO RECEIVE should be amount ${amount.toDouble()} - ${offerFee.amount.toDouble()}",
                    amountToReceiveInIncomingForm.amount.toDouble(),
                    Matchers.equalTo(amount.toDouble() - offerFee.amount.toDouble())
                )

                MatcherAssert.assertThat(
                    "AMOUNT TO SEND should be amount $amount",
                    amountToSendInIncomingForm.amount.toDouble(),
                    Matchers.equalTo(amount.toDouble())
                )

                MatcherAssert.assertThat(
                    "FEE size should be amount $feeSizeForTaker",
                    offerFee.amount.toDouble(),
                    Matchers.equalTo(feeSizeForTaker.toDouble())
                )

                alert { checkErrorAlert() }
                signAndSubmitMessage(userTwo as DefaultUser, userTwo.otfWallet.secretKey)

                alert { checkErrorAlert() }
            }
        }
    }
}