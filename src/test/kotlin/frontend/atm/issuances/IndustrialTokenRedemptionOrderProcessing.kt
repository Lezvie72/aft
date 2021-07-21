package frontend.atm.issuances

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import models.CoinType
import org.apache.commons.lang.RandomStringUtils
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import pages.atm.AtmIssuancesPage
import pages.atm.AtmMarketplacePage
import ru.yandex.qatools.htmlelements.element.Button
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import utils.helpers.to
import java.math.BigDecimal

@Tags(Tag(TagNames.Epic.ISSUANCE.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Industrial token. Redemption order processing")
@TmsLink("ATMCH-1697")
class IndustrialTokenRedemptionOrderProcessing : BaseTest() {

    private val userOne = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val itIssuer = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS

    @TmsLink("ATMCH-3267")
    @Test
    @DisplayName("Industrial token. Redemption order processing. Field validation")
    fun industrialTokenRedemptionOrderProcessingFieldValidation() {
        val amount = BigDecimal("10.${RandomStringUtils.randomNumeric(8)}")
        val secretKey =
            "d8937be1d83f73abb68beae10d99bccb481704a9a075932e162c00039fd8fd9808856efbbbc44c4b5476596da89cf500266c789ce054a221a1eaa7fa41465"

        openPage<AtmMarketplacePage>(driver) { submit(userOne) }
            .buyOrReceiveToken(CoinType.IT, amount.toString(), userOne, userOne.mainWallet)
        openPage<AtmMarketplacePage>(driver).logout()

        with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
            e {
                chooseToken(CoinType.IT)
                val currentQueue = wait {
                    untilPresented<WebElement>(By.xpath(".//atm-dist-deal//div[contains(text(),'Current queue')]/ancestor::div[2]//button[1]"))
                }.to<Button>("requestCurrentQueue")

                click(currentQueue)
            }
            val myOffer = requestOffers.find {
                it.totalRequestedAmount == amount
            } ?: error("Can't find offer with unit price '$amount'")
            myOffer.clickProceedButton()
            e {
                click(approve)
            }
            signAndSubmitMessage(itIssuer, secretKey)
            assert { elementContainingTextPresented("Invalid key") }
        }
    }


    @TmsLink("ATMCH-3213")
    @Test
    @DisplayName("Industrial token. Redemption order processing. Filter")
    fun industrialTokenRedemptionOrderProcessingFilter() {
        val amount = BigDecimal("10.${RandomStringUtils.randomNumeric(8)}")

        step("Precondition. Create order.") {
            openPage<AtmMarketplacePage>(driver) { submit(userOne) }
                .buyOrReceiveToken(CoinType.IT, amount.toString(), userOne, userOne.mainWallet)
            openPage<AtmMarketplacePage>(driver).logout()
        }

        with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
            e {
                chooseToken(CoinType.IT)
                val currentQueue = wait {
                    untilPresented<WebElement>(By.xpath(".//atm-dist-deal//div[contains(text(),'Current queue')]/ancestor::div[2]//button[1]"))
                }.to<Button>("requestCurrentQueue")

                click(currentQueue)
                select(filterByStatus, "SUBMITTED")
            }

            val myOffer = requestOffers.find {
                it.totalRequestedAmount == amount
            } ?: error("Can't find offer with unit price '$amount'")

            assert { elementPresented(myOffer) }
        }
    }
}