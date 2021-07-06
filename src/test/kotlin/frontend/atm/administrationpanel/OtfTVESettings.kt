package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.*
import models.OtfAmounts
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.*
import pages.atm.AtmAdminTveSettingsPage.Headers.RATE
import pages.atm.AtmAdminTveSettingsPage.Headers.TOKEN
import ru.yandex.qatools.htmlelements.element.Button
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.to
import java.math.BigDecimal

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.OTCSETTINGS))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("OTF. TVE Settings")
class OtfTVESettings : BaseTest() {

    private val tokenFiat = FIAT
    private val baseAsset = CC
    private val quoteAsset = VT
    private val unitPriceValue = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
    private val amountCount = OtfAmounts.AMOUNT_10.amount
    private val userOne = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
    private val userTwo = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET
    private val wallet = userOne.mainWallet

    @TmsLink("ATMCH-5961")
    @Test
    @DisplayName("TVE settings. Disable rate source for token.")
    fun tveSettingsDisableRateSourceForToken() {
        with(openPage<AtmAdminTveSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {

            val row = tokenAndRate.find { it[TOKEN]?.text == tokenFiat.tokenSymbol }?.get(
                TOKEN
            )?.to<Button>("Row rate with token $TOKEN")
                ?: error("Can't find row rate with token symbol: $TOKEN")

            e {
                click(row)
                click(usdEquivalentSettings)
            }
            assert {
                elementPresented(fixed)
                elementPresented(disabled)
                elementPresented(fromMarketData)
            }
            e {
                click(fixed)
                sendKeys(usdEquivalent, "1")
                click(confirm)
            }

            val row1 = tokenAndRate.find { it[TOKEN]?.text == tokenFiat.tokenSymbol }?.get(
                TOKEN
            )?.to<Button>("Row rate with token $TOKEN")
                ?: error("Can't find row rate with token symbol: $TOKEN")

            e {
                click(row1)
                click(usdEquivalentSettings)
                click(disabled)
                click(confirm)
            }

            val row2 = tokenAndRate.find { it[TOKEN]?.text == tokenFiat.tokenSymbol }
                ?: error("Can't find row rate with token symbol: $TOKEN")
            val rate = row2[RATE]?.text

            assertThat(
                "No row found with '$tokenFiat'",
                rate,
                Matchers.hasToString("0".toLowerCase())
            )
        }
    }

    @TmsLink("ATMCH-4218")
    @Test
    @DisplayName("TVE settings.Excluding tokens from price corridor check.")
    fun tveSettingsExcludingTokensFromPriceCorridorCheck() {

        with(openPage<AtmAdminTveSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {

            if (checkStateForToken(quoteAsset) == "true") {
                setStateForToken(quoteAsset, false)
            }
        }

        with(openPage<AtmStreamingPage>(driver) { submit(userTwo) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "${quoteAsset.tokenSymbol}/${baseAsset.tokenSymbol}",
                    "$amountCount ${quoteAsset.tokenSymbol}",
                    unitPriceValue.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    userTwo
                )
            }
        }
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmStreamingPage>(driver) { submit(userOne) }) {
            e {
                click(overview)
                setFilterToday(quoteAsset, baseAsset)
                findAndOpenOfferInOverview(unitPriceValue)
                wait {
                    untilPresented(offerDetailsLabel)
                }
                acceptOffer(userOne)
                assertThat(
                    "Offer with unit price $unitPrice should not be exist",
                    !isOfferExist(unitPriceValue, overviewOffersList)
                )
            }
        }
    }

    @TmsLink("ATMCH-4216")
    @Test
    @DisplayName("TVE settings.Overriding of token reference rate")
    fun tveSettingsOverridingOfTokenReferenceRate() {

        val (financialIDFirst, coefficientValueFirst) = with(openPage<AtmAdminTveSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {

            editUsdEquivalent(baseAsset, AtmAdminTokensPage.EquivalentType.FROM_MARKET, "1", "Au")
            val row = tokenAndRate.find { it[TOKEN]?.text == baseAsset.tokenSymbol }?.get(TOKEN)
                ?.to<Button>("Row rate with token $TOKEN")
                ?: error("Can't find row rate with token symbol: $TOKEN")
            e {
                click(row)
                click(usdEquivalentSettings)
                e {
                    until("Wait for hidden preloader", 5L) {
                        usdEquivalentOverlay.preloader.getAttribute("style") == "visibility: hidden;"
                    }
                    click(fromMarketData)
                    select(financialId, "Au")
                    sendKeys(coefficient, "1")
                }
                val financialIDFirst = financialIDValue.text
                val coefficientValueFirst = coefficientValue.text
                e {
                    click(confirm)
                    Thread.sleep(10000)
                }
                financialIDFirst to coefficientValueFirst
            }

        }

        with(openPage<AtmMarketplacePage>(driver) { submit(userOne) }) {
            e {
                chooseToken(baseAsset)
                click(newOrderButton)
                select(selectWallet, wallet.publicKey)
                val amount = "2"
                sendKeys(tokenQuantity, amount)
                click(submitButton)
            }
            signAndSubmitMessage(userOne.oAuthSecret, wallet.secretKey)
            assert {
                elementWithTextPresented(" Order completed successfully ")
            }
        }


        with(openPage<AtmAdminTveSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {

            editUsdEquivalent(baseAsset, AtmAdminTokensPage.EquivalentType.FIXED, "1", "Au")

        }

        with(openPage<AtmMarketplacePage>(driver) { submit(userOne) }) {
            e {
                chooseToken(baseAsset)
                click(newOrderButton)
                select(selectWallet, wallet.publicKey)
                val amount = "2"
                sendKeys(tokenQuantity, amount)
                click(submitButton)
            }
            signAndSubmitMessage(userOne.oAuthSecret, wallet.secretKey)
            assert {
                elementWithTextPresented(" Order completed successfully ")
            }
        }

        val (financialIDSecond, coefficientValueSecond) = with(openPage<AtmAdminTveSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {

            editUsdEquivalent(baseAsset, AtmAdminTokensPage.EquivalentType.FROM_MARKET, "1", "Au")
            val row = tokenAndRate.find { it[TOKEN]?.text == baseAsset.tokenSymbol }?.get(TOKEN)
                ?.to<Button>("Row rate with token $TOKEN")
                ?: error("Can't find row rate with token symbol: $TOKEN")
            e {
                click(row)
                click(usdEquivalentSettings)
                e {
                    until("Wait for hidden preloader", 5L) {
                        usdEquivalentOverlay.preloader.getAttribute("style") == "visibility: hidden;"
                    }
                    click(fromMarketData)
                    select(financialId, "Au")
                }
                val financialIDSecond = financialIDValue.text
                val coefficientValueSecond = coefficientValue.text

                financialIDSecond to coefficientValueSecond
            }

        }
        with(openPage<AtmAdminTveSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {

            editUsdEquivalent(baseAsset, AtmAdminTokensPage.EquivalentType.FIXED, "1", "Au")

        }

        assertThat(
            "Financial ID should be equals",
            financialIDFirst,
            Matchers.equalTo(financialIDSecond)
        )
        assertThat(
            "Coefficient value should be equals",
            coefficientValueFirst,
            Matchers.equalTo(coefficientValueSecond)
        )
    }

}


