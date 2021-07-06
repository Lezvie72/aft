

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.CC
import models.CoinType.VT
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.AtmProfilePage
import pages.atm.AtmRFQPage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal


@Tags(Tag(TagNames.Flow.OTC),Tag(TagNames.Epic.RFQ.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("RFQ")
@Story("RFQ. Placing new request for quote")
class PlacingNewRequestForQuote : BaseTest() {
    private val userOne = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val userTwo = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04
    private val userThree = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET
    private val baseAsset = CC
    private val quoteAsset = VT

    @TmsLink("ATMCH-5885")
    @Test
    @DisplayName("RFQ. Placing new request (negative)")
    fun placingNewRequestNegative() {
        val errorText1 = "Insufficient balance"
        val errorText2 = "Base and quote assets cannot be the same"
        val errorText3 = "Invalid key"
        val amount = "1"
        with(openPage<AtmRFQPage>(driver) { submit(userThree) }) {
            e {
                click(createRequest)
                click(iWantToSellAsset)
                select(assetToSend, CC.tokenSymbol)
                sendKeysAndEnter(amountToSend, "9999999")
            }
            assert {
                elementWithTextPresented(errorText1)
            }
            e {
                select(assetToReceive, CC.tokenSymbol)
            }
            assert {
                elementContainingTextPresented(errorText2)
            }
            e {
                select(assetToReceive, VT.tokenSymbol)
                amountToSend.clear()
                sendKeysAndEnter(amountToSend, "99999991")
            }
            assert {
                elementWithTextPresented(errorText1)
            }
            e{
                deleteData(amountToSend)
                sendKeys(amountToSend, amount)
                click(createRequestFromForm)
                signMessage("111")
            }
            assert {
                elementContainingTextPresented(errorText3)
            }
        }
    }


    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-5858")
    @Test
    @DisplayName("RFQ. Placing new request for selected participants")
    fun placingNewRequestForSelectedParticipants() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")

        step("Precondition - create offer") {
            val companyName = step("Get company name for user ${userTwo.email}") {
                openPage<AtmProfilePage>(driver) { submit(userTwo) }.getCompanyName()
            }
            openPage<AtmWalletPage>(driver).logout()

            with(openPage<AtmRFQPage>(driver) { submit(userOne) }) {
                e {
                    createRFQ(
                        AtmRFQPage.OperationType.BUY,
                        baseAsset,
                        quoteAsset,
                        amount,
                        "1",
                        userOne,
                        userTwo,
                        companyName
                    )

                    assertThat(
                        "Offer with ${amount.toDouble()} should be visible after created",
                        isOfferExistOutgoing(amount)
                    )
                }
            }
            openPage<AtmWalletPage>(driver).logout()
        }

        step("Check incoming offers for user ${userTwo.email}") {
            with(openPage<AtmRFQPage>(driver) { submit(userTwo) }) {
                e {
                    click(viewRequest)
                    assertThat(
                        "Offer should be visible for user ${userTwo.email}",
                        isOfferExistIncoming(amount)
                    )
                    openPage<AtmWalletPage>(driver).logout()
                }
            }
        }

        step("Check incoming offers for user ${userThree.email}") {
            with(openPage<AtmRFQPage>(driver) { submit(userThree) }) {
                e {
                    click(viewRequest)
                    assertThat(
                        "Offer should be visible for user ${userThree.email}",
                        !isOfferExistIncoming(amount)
                    )
                }
            }
        }
    }
}
