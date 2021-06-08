package frontend.atm.otс.streaming

import frontend.BaseTest
import io.qameta.allure.*
import models.CoinType
import models.OtfAmounts
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import org.openqa.selenium.WebDriver
import pages.atm.AtmStreamingPage
import pages.core.actions.ElementActions
import pages.core.actions.isVisibilitySafety
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import java.math.BigDecimal

@Tags(Tag(TagNames.Flow.OTC),Tag(TagNames.Epic.STREAMING.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Streaming")
@Story("Cancellation of the offer")
class CancellationOfTheOffer : BaseTest() {
    // preconditions
    private val baseAsset = CoinType.VT
    private val quoteAsset = CoinType.CC
    private val amountBuy = OtfAmounts.AMOUNT_10.amount
    private val userOne2FA = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_OTF_WALLET))
    @TmsLink("ATMCH-5958")
    @Test
    @DisplayName("Steaming. Validation during offer cancellation")
    fun validationDuringOfferCancellation() {
        val unitPriceOffer = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val errorAlertLinkPK = "//div[contains(text(),' Invalid key ')]"
        val errorAlertLinkSK = "//div[contains(text(),' Wrong code ')]"
        val invalidPrivateKey = "12345678bb4992acf09c9cba9e266c696aff77fca923db2a472b813e37f9e96f"
        val invalidSecretKey = "123456HTFP3ZDFW7"


        with(utils.helpers.openPage<AtmStreamingPage>(driver) { submit(userOne2FA) }) {
            createStreaming(
                AtmStreamingPage.OperationType.BUY,
                "$baseAsset/$quoteAsset",
                "$amountBuy $baseAsset",
                unitPriceOffer.toString(),
                AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                userOne2FA
            )

            e {
                click(myOffers)
                setFilterBuyToday(this, this@with)
                findAndOpenOfferInOfferList(unitPriceOffer)

                wait {
                    untilPresented(offerDetailsLabel)
                }

                click(cancelOfferButton)

                assert {
                    elementEnabled(cancelOfferLabel)
                }

                // not valid secret key
                sendKeys(privateKey, invalidPrivateKey)
                click(confirmPrivateKeyButton)

                MatcherAssert.assertThat("Error message is shown", isVisibilitySafety(page, errorAlertLinkPK))
                MatcherAssert.assertThat(
                    "Confirm button is disabled",
                    confirmButtonInDialogWindow.getAttribute("disabled").contains("true")
                )

                clear(privateKey)
                sendKeys(privateKey, userOne2FA.otfWallet.secretKey)
                click(confirmPrivateKeyButton)

                assert {
                    elementEnabled(confirmationLabel)
                }

                // not valid secret key
                enterConfirmationCode(invalidSecretKey)

                MatcherAssert.assertThat("Wrong code message is shown", isVisibilitySafety(page, errorAlertLinkSK))

                click(cancelSubmitPrivateKeyButton)
                click(myOffers)
                setFilterBuyToday(this, this@with)
                findAndOpenOfferInOfferList(unitPriceOffer)
            }
        }
    }

    @Step("Set filter buy and today")
    private fun setFilterBuyToday(
        elementActions: ElementActions<WebDriver>,
        atmStreamingPage: AtmStreamingPage
    ) {
        elementActions.click(atmStreamingPage.resetFilters)
        elementActions.click(atmStreamingPage.showBuyOnly)
        elementActions.select(atmStreamingPage.tradingPair, "$baseAsset/$quoteAsset")
        elementActions.click(atmStreamingPage.dateFrom)
        elementActions.click(atmStreamingPage.today)
    }
}