package frontend.atm.otс.rfq

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmProfilePage
import pages.atm.AtmRFQPage
import pages.atm.AtmWalletPage
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal


@Tags(Tag(TagNames.Flow.OTC),Tag(TagNames.Epic.RFQ.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("RFQ")
@Story("RFQ. Cancellation of a request for quote (RFQ) by the participant who placed it")
class CancellationOfRequestForQuoteRFQ : BaseTest() {
    private val industrialUserOne = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val industrialUserTwo = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04
    private val firstToken = CoinType.CC
    private val secondToken = CoinType.VT

    @TmsLink("ATMCH-834")
    @Test
    @DisplayName("RFQ. Cancellation of a quote request by participant")
    fun rfqCancellationOfaQuoteRequestByParticipant() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")

        step("Precondition - create offer") {
            val companyName =
                openPage<AtmProfilePage>(driver) { submit(industrialUserTwo) }.companyName.text

            openPage<AtmWalletPage>(driver).logout()

            with(openPage<AtmRFQPage>(driver) { submit(industrialUserOne) }) {
                e {
                    createRFQ(
                        AtmRFQPage.OperationType.BUY,
                        firstToken,
                        secondToken,
                        amount,
                        "1",
                        industrialUserOne,
                        industrialUserTwo,
                        companyName
                    )

                    MatcherAssert.assertThat(
                        "Offer with ${amount.toDouble()} should be visible after created",
                        isOfferExistOutgoing(amount)
                    )
                }
            }

            openPage<AtmWalletPage>(driver).logout()
        }

        step("Check offer - is visible for user $industrialUserTwo") {
            with(openPage<AtmRFQPage>(driver) { submit(industrialUserTwo) }) {
                e {
                    click(viewRequest)
                    MatcherAssert.assertThat(
                        "Offer with ${amount.toDouble()} should be visible for user ${industrialUserTwo.email}",
                        isOfferExistIncoming(amount)
                    )
                }
            }
            openPage<AtmWalletPage>(driver).logout()
        }

        step("Cancel offer with amount $amount") {
            with(openPage<AtmRFQPage>(driver) { submit(industrialUserOne) }) {
                e {
                    cancelRFQ(amount, industrialUserOne)
                }
            }
            openPage<AtmWalletPage>(driver).logout()
        }

        step("Check offer - is visible for user $industrialUserTwo") {
            with(openPage<AtmRFQPage>(driver) { submit(industrialUserTwo) }) {
                e {
                    click(viewRequest)
                    MatcherAssert.assertThat(
                        "Offer with ${amount.toDouble()} should not be visible for user ${industrialUserTwo.email}",
                        !isOfferExistIncoming(amount)
                    )
                }
            }
        }
    }
}
